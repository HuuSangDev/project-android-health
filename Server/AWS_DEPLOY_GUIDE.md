 # Hướng dẫn Deploy SelfCare API lên AWS EC2

## Bước 1: Chuẩn bị trên máy local

### 1.1 Kiểm tra file Firebase

Đảm bảo file `serviceAccountKey.json` đã có trong:

```
Server/src/main/resources/firebase/serviceAccountKey.json
```

### 1.2 Cài Docker Desktop

- Windows: https://docs.docker.com/desktop/install/windows-install/
- Sau khi cài, mở Docker Desktop và đợi nó chạy

### 1.3 Tạo tài khoản Docker Hub (miễn phí)

- Vào https://hub.docker.com/ → Sign Up
- Ghi nhớ username của bạn

---

## Bước 2: Build Docker Image (trên máy local)

Mở Terminal/PowerShell, chạy:

```powershell
# Vào thư mục Server
cd D:\UTE\Andoir_java\Thay Huy\Androi_App\Server

# Login Docker Hub
docker login
# Nhập username và password Docker Hub

# Build image (thay YOUR_DOCKERHUB_USERNAME bằng username của bạn)
docker build -t YOUR_DOCKERHUB_USERNAME/selfcare-api:latest .

# Push lên Docker Hub
docker push YOUR_DOCKERHUB_USERNAME/selfcare-api:latest
```

**Ví dụ**: Nếu Docker Hub username là `sangletop`:

```powershell
docker build -t sangletop/selfcare-api:latest .
docker push sangletop/selfcare-api:latest
```

---

## Bước 3: Tạo EC2 Instance trên AWS

### 3.1 Vào AWS Console

- https://console.aws.amazon.com/
- Tìm "EC2" → Launch Instance

### 3.2 Cấu hình Instance

```
Name: selfcare-api-server
AMI: Amazon Linux 2023
Instance type: t2.small (hoặc t2.micro cho free tier)
Key pair: Tạo mới → Download file .pem
```

### 3.3 Network Settings

- Auto-assign public IP: **Enable**
- Security group → Create new:
  - SSH (22) - Source: My IP
  - Custom TCP (8080) - Source: 0.0.0.0/0

### 3.4 Storage

- 20 GB gp3

### 3.5 Launch Instance

- Ghi lại **Public IP** của instance

---

## Bước 4: Cài Docker trên EC2

### 4.1 SSH vào EC2

**Windows (PowerShell)**:

```powershell
ssh -i "path/to/your-key.pem" ec2-user@YOUR_EC2_PUBLIC_IP
```

**Ví dụ**:

```powershell
ssh -i "C:\Users\Sang\Downloads\selfcare-key.pem" ec2-user@54.123.45.67
```

### 4.2 Cài Docker

```bash
# Update system
sudo yum update -y

# Install Docker
sudo yum install -y docker

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -aG docker ec2-user

# QUAN TRỌNG: Logout và login lại
exit
```

### 4.3 SSH lại và verify

```bash
ssh -i "your-key.pem" ec2-user@YOUR_EC2_PUBLIC_IP
docker --version
```

---

## Bước 5: Chạy Container trên EC2

### 5.1 Pull image từ Docker Hub

```bash
docker pull YOUR_DOCKERHUB_USERNAME/selfcare-api:latest
```

### 5.2 Chạy container với environment variables

```bash
docker run -d \
  --name selfcare-api \
  -p 8080:8080 \
  --restart unless-stopped \
  -e DATABASE_URL="jdbc:postgresql://dpg-d55pa3muk2gs73c33dr0-a.singapore-postgres.render.com:5432/selfcare_app?sslmode=require" \
  -e DATABASE_USERNAME="root" \
  -e DATABASE_PASSWORD="2m5OydRh8hIBY50BuhYlvZwoTsuBxXLZ" \
  -e REDIS_HOST="singapore-keyvalue.render.com" \
  -e REDIS_PORT="6379" \
  -e REDIS_USERNAME="red-d4lefsuuk2gs738c1a90" \
  -e REDIS_PASSWORD="D1tU4cPMtKqaatXv0PJ4XMSk3qsLexXw" \
  -e JWT_SIGNER_KEY="HAGUOt0Qm87yn2SFEfjlVRM3chXO/Wpy1YnsdZQz2O7+S4dPFiTQvpxHVVLE6r+c" \
  -e MAIL_USERNAME="sangletop125@gmail.com" \
  -e MAIL_PASSWORD="rshz pojo oczc dcbh" \
  -e CLOUDINARY_CLOUD_NAME="dg9ke49vm" \
  -e CLOUDINARY_API_KEY="958314775373525" \
  -e CLOUDINARY_API_SECRET="eZpxIv1RZm4k5QzEiSgz1iUmQIQ" \
  -e GEMINI_API_KEY="AIzaSyAwgLtKVohLdkm3Kozdo9GNOSnY8-4GxdI" \
  YOUR_DOCKERHUB_USERNAME/selfcare-api:latest
```

### 5.3 Kiểm tra container

```bash
# Xem container đang chạy
docker ps

# Xem logs (Ctrl+C để thoát)
docker logs -f selfcare-api

# Test API
curl http://localhost:8080/app/actuator/health
```

---

## Bước 6: Cập nhật Android App

### 6.1 Cập nhật ApiClient.java

Tìm file `ApiClient.java` và đổi BASE_URL:

```java
private static final String BASE_URL = "http://YOUR_EC2_PUBLIC_IP:8080/";
```

### 6.2 Cập nhật WebSocketManager.java

```java
private static final String WS_URL = "ws://YOUR_EC2_PUBLIC_IP:8080/ws/websocket";
```

---

## Các lệnh Docker hữu ích

```bash
# Xem logs
docker logs -f selfcare-api

# Restart container
docker restart selfcare-api

# Stop container
docker stop selfcare-api

# Xóa container
docker rm selfcare-api

# Update image mới (khi có thay đổi code)
docker pull YOUR_DOCKERHUB_USERNAME/selfcare-api:latest
docker stop selfcare-api
docker rm selfcare-api
# Chạy lại lệnh docker run ở Bước 5.2
```

---

## Troubleshooting

### Lỗi "permission denied" khi chạy docker

```bash
sudo usermod -aG docker ec2-user
exit
# SSH lại
```

### Container không start

```bash
docker logs selfcare-api
```

### Không kết nối được từ Android

- Kiểm tra Security Group có mở port 8080
- Kiểm tra Public IP đúng chưa
- Đảm bảo dùng `http://` (không phải `https://`)

---

## Tóm tắt

1. **Local**: `docker build` → `docker push`
2. **EC2**: `docker pull` → `docker run` với env variables
3. **Android**: Đổi URL sang EC2 Public IP
