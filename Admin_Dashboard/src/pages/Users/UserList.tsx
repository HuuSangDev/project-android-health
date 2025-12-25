import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../services/userService';
import { User } from '../../types/health';
import PageMeta from '../../components/common/PageMeta';
import toast from 'react-hot-toast';

export default function UserList() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGoal, setSelectedGoal] = useState<string>('ALL');
  const { t } = useTranslation();

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await userService.getAllUsers();
      setUsers(data);
    } catch (error) {
      console.error('Failed to load users:', error);
      toast.error('Không thể tải danh sách người dùng');
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (userId: number) => {
    if (window.confirm('Bạn có chắc muốn xóa người dùng này?')) {
      try {
        await userService.deleteUser(userId);
        setUsers(users.filter(user => user.id !== userId));
        toast.success('Xóa người dùng thành công');
      } catch (error) {
        toast.error('Không thể xóa người dùng');
      }
    }
  };

  const calculateBMI = (weight?: number, height?: number) => {
    if (!weight || !height) return 'N/A';
    const heightInMeters = height / 100;
    return (weight / (heightInMeters * heightInMeters)).toFixed(1);
  };

  const getBMICategory = (bmi: string) => {
    if (bmi === 'N/A') return { category: 'N/A', color: 'text-gray-500' };
    const bmiNum = parseFloat(bmi);
    if (bmiNum < 18.5) return { category: 'Thiếu cân', color: 'text-blue-600' };
    if (bmiNum < 25) return { category: 'Bình thường', color: 'text-green-600' };
    if (bmiNum < 30) return { category: 'Thừa cân', color: 'text-yellow-600' };
    return { category: 'Béo phì', color: 'text-red-600' };
  };

  const filteredUsers = users.filter(user => {
    const matchesSearch = user.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         user.email?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesGoal = selectedGoal === 'ALL' || user.userProfileResponse?.healthGoal === selectedGoal;
    return matchesSearch && matchesGoal;
  });

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-brand-500"></div>
      </div>
    );
  }

  return (
    <>
      <PageMeta
        title={`${t('users.title')} | Health Care Admin`}
        description={t('users.subtitle')}
      />
      
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">{t('users.title')}</h1>
            <p className="text-gray-500 dark:text-gray-400">{t('users.subtitle')}</p>
          </div>
          <div className="text-sm text-gray-500">
            Tổng: {users.length} người dùng
          </div>
        </div>

        {/* Filters */}
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="relative flex-1">
            <svg className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              type="text"
              placeholder={t('users.searchUsers')}
              className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 focus:border-transparent dark:border-gray-700 dark:bg-gray-800 dark:text-white"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <select
            className="px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 focus:border-transparent dark:border-gray-700 dark:bg-gray-800 dark:text-white"
            value={selectedGoal}
            onChange={(e) => setSelectedGoal(e.target.value)}
          >
            <option value="ALL">{t('users.allGoals')}</option>
            <option value="WEIGHT_LOSS">{t('users.weightLoss')}</option>
            <option value="MUSCLE_GAIN">{t('users.muscleGain')}</option>
            <option value="MAINTAIN_WEIGHT">{t('users.maintainWeight')}</option>
          </select>
        </div>

        {/* Users Table */}
        <div className="rounded-2xl border border-gray-200 bg-white dark:border-gray-800 dark:bg-white/[0.03] overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 dark:bg-gray-800/50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    Người dùng
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    Thông tin sức khỏe
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    BMI
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    Mục tiêu
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    Mức độ hoạt động
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    Hành động
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                {filteredUsers.map((user) => {
                  const profile = user.userProfileResponse;
                  const bmi = calculateBMI(profile?.weight, profile?.height);
                  const bmiInfo = getBMICategory(bmi);
                  
                  return (
                    <tr key={user.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-10 w-10">
                            {profile?.avatarUrl ? (
                              <img 
                                className="h-10 w-10 rounded-full object-cover" 
                                src={profile.avatarUrl} 
                                alt={user.fullName} 
                              />
                            ) : (
                              <div className="h-10 w-10 rounded-full bg-brand-100 dark:bg-brand-900 flex items-center justify-center">
                                <span className="text-sm font-medium text-brand-600 dark:text-brand-400">
                                  {user.fullName?.split(' ').map(n => n[0]).join('') || '?'}
                                </span>
                              </div>
                            )}
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900 dark:text-white">
                              {user.fullName || 'Chưa cập nhật'}
                            </div>
                            <div className="text-sm text-gray-500 dark:text-gray-400">
                              {user.email}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                        <div>
                          <div>{profile?.gender || 'N/A'}</div>
                          <div>
                            {profile?.height ? `${profile.height}cm` : 'N/A'}, 
                            {profile?.weight ? ` ${profile.weight}kg` : ' N/A'}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm">
                          <div className="font-medium text-gray-900 dark:text-white">{bmi}</div>
                          <div className={`text-xs ${bmiInfo.color}`}>{bmiInfo.category}</div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                          profile?.healthGoal === 'WEIGHT_LOSS' ? 'bg-blue-100 text-blue-800' :
                          profile?.healthGoal === 'MUSCLE_GAIN' ? 'bg-purple-100 text-purple-800' :
                          profile?.healthGoal === 'MAINTAIN_WEIGHT' ? 'bg-green-100 text-green-800' :
                          'bg-gray-100 text-gray-800'
                        }`}>
                          {profile?.healthGoal?.replace('_', ' ') || 'Chưa đặt'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                          profile?.activityLevel === 'HIGH' ? 'bg-green-100 text-green-800' :
                          profile?.activityLevel === 'MODERATE' ? 'bg-yellow-100 text-yellow-800' :
                          profile?.activityLevel === 'LOW' ? 'bg-red-100 text-red-800' :
                          'bg-gray-100 text-gray-800'
                        }`}>
                          {profile?.activityLevel || 'N/A'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <div className="flex space-x-2">
                          <button className="text-brand-600 hover:text-brand-800" title="Xem chi tiết">
                            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                          </button>
                          <button
                            onClick={() => handleDelete(user.id)}
                            className="text-red-600 hover:text-red-800"
                            title="Xóa"
                          >
                            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {filteredUsers.length === 0 && !loading && (
          <div className="text-center py-12">
            <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
            </svg>
            <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">Không tìm thấy người dùng</h3>
            <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
              Thử điều chỉnh bộ lọc tìm kiếm.
            </p>
          </div>
        )}
      </div>
    </>
  );
}
