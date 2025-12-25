# Health Care Admin Dashboard

A modern React.js admin dashboard for managing health care applications, built with TypeScript, Tailwind CSS, and integrated with Spring Boot backend.

## Features

### 🏥 Health Management
- **Dashboard**: Overview of users, foods, exercises, and health metrics
- **User Management**: View and manage user profiles, BMI tracking, health goals
- **Food Management**: Manage food items, nutrition information, meal planning
- **Exercise Management**: Manage exercises, workout routines, fitness programs

### 🔐 Authentication
- Secure login system with JWT token support
- Protected routes with authentication guards
- User session management

### 🎨 Modern UI/UX
- Responsive design with Tailwind CSS
- Dark/Light theme support
- Interactive charts and data visualization
- Mobile-friendly interface

### 🔧 Technical Features
- TypeScript for type safety
- React Router for navigation
- Axios for API integration
- React Hot Toast for notifications
- Form validation with React Hook Form

## Getting Started

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn
- Spring Boot backend running on `http://localhost:8080/app`

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd free-react-tailwind-admin-dashboard
```

2. Install dependencies:
```bash
npm install --legacy-peer-deps
```

3. Start the development server:
```bash
npm run dev
```

4. Open your browser and navigate to `http://localhost:5173`

### Demo Credentials
- **Email**: admin@healthcare.com
- **Password**: admin123

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── auth/           # Authentication components
│   ├── health/         # Health-specific components
│   ├── header/         # Header components
│   └── ui/             # Generic UI components
├── context/            # React contexts (Auth, Theme, etc.)
├── pages/              # Page components
│   ├── Dashboard/      # Dashboard pages
│   ├── Foods/          # Food management pages
│   ├── Exercises/      # Exercise management pages
│   ├── Users/          # User management pages
│   └── AuthPages/      # Authentication pages
├── services/           # API service layer
├── types/              # TypeScript type definitions
└── layout/             # Layout components
```

## API Integration

The dashboard integrates with a Spring Boot backend running on `http://localhost:8080/app`. The following endpoints are used:

- `GET /foods/all` - Get all foods
- `GET /exercises/all` - Get all exercises
- `GET /users/all` - Get all users (mock data for demo)
- Authentication endpoints (to be implemented)

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Technologies Used

- **Frontend**: React 19, TypeScript, Tailwind CSS
- **Routing**: React Router v7
- **State Management**: React Context API
- **HTTP Client**: Axios
- **Charts**: ApexCharts
- **Icons**: Custom SVG icons
- **Notifications**: React Hot Toast
- **Forms**: React Hook Form

## Backend Integration

This dashboard is designed to work with the Android Health Care app's Spring Boot backend. Make sure the backend server is running on `http://localhost:8080/app` before using the dashboard.

### API Authentication
The dashboard uses Bearer token authentication. Tokens are stored in localStorage and automatically included in API requests.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.