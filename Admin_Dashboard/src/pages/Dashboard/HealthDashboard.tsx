import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { foodService } from '../../services/foodService';
import { exerciseService } from '../../services/exerciseService';
import { userService } from '../../services/userService';
import PageMeta from '../../components/common/PageMeta';
import { Link } from 'react-router';

interface DashboardStats {
  totalUsers: number;
  totalFoods: number;
  totalExercises: number;
}

export default function HealthDashboard() {
  const [stats, setStats] = useState<DashboardStats>({ totalUsers: 0, totalFoods: 0, totalExercises: 0 });
  const [loading, setLoading] = useState(true);
  const { t } = useTranslation();

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [foods, exercises, users] = await Promise.all([
        foodService.getAllFoods().catch(() => []),
        exerciseService.getAllExercises().catch(() => []),
        userService.getAllUsers().catch(() => []),
      ]);
      setStats({
        totalUsers: users.length,
        totalFoods: foods.length,
        totalExercises: exercises.length,
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-brand-500"></div>
      </div>
    );
  }

  return (
    <>
      <PageMeta title={`${t('navigation.dashboard')} | Health Care Admin`} description="Health Care Admin Dashboard" />
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800 dark:text-white">{t('navigation.dashboard')}</h1>
          <p className="text-gray-500 dark:text-gray-400">Tổng quan hệ thống quản lý sức khỏe</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Tổng người dùng</p>
                <h3 className="text-3xl font-bold text-gray-800 dark:text-white mt-1">{stats.totalUsers}</h3>
              </div>
              <div className="text-4xl">👥</div>
            </div>
          </div>
          <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Tổng món ăn</p>
                <h3 className="text-3xl font-bold text-gray-800 dark:text-white mt-1">{stats.totalFoods}</h3>
              </div>
              <div className="text-4xl">🍎</div>
            </div>
          </div>
          <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Tổng bài tập</p>
                <h3 className="text-3xl font-bold text-gray-800 dark:text-white mt-1">{stats.totalExercises}</h3>
              </div>
              <div className="text-4xl">💪</div>
            </div>
          </div>
        </div>

        {/* Quick Links */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Link to="/users" className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03] hover:shadow-lg transition-shadow">
            <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{t('navigation.userManagement')}</h3>
            <p className="text-sm text-gray-500 mt-1">Quản lý thông tin người dùng</p>
            <span className="text-brand-500 text-sm mt-4 inline-block">Xem chi tiết →</span>
          </Link>
          <Link to="/foods" className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03] hover:shadow-lg transition-shadow">
            <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{t('navigation.foodManagement')}</h3>
            <p className="text-sm text-gray-500 mt-1">Quản lý thực phẩm và dinh dưỡng</p>
            <span className="text-brand-500 text-sm mt-4 inline-block">Xem chi tiết →</span>
          </Link>
          <Link to="/exercises" className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03] hover:shadow-lg transition-shadow">
            <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{t('navigation.exerciseManagement')}</h3>
            <p className="text-sm text-gray-500 mt-1">Quản lý bài tập và luyện tập</p>
            <span className="text-brand-500 text-sm mt-4 inline-block">Xem chi tiết →</span>
          </Link>
        </div>
      </div>
    </>
  );
}
