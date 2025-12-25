import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { foodService } from '../../services/foodService';
import { Food } from '../../types/health';
import PageMeta from '../../components/common/PageMeta';
import toast from 'react-hot-toast';

export default function FoodList() {
  const [foods, setFoods] = useState<Food[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedMealType, setSelectedMealType] = useState<string>('ALL');
  const { t } = useTranslation();

  useEffect(() => {
    loadFoods();
  }, []);

  const loadFoods = async () => {
    try {
      setLoading(true);
      const data = await foodService.getAllFoods();
      setFoods(data);
    } catch (error) {
      console.error('Failed to load foods:', error);
      toast.error('Không thể tải danh sách thực phẩm');
      setFoods([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Bạn có chắc muốn xóa món ăn này?')) {
      try {
        await foodService.deleteFood(id);
        setFoods(foods.filter(food => food.foodId !== id));
        toast.success('Xóa món ăn thành công');
      } catch (error) {
        toast.error('Không thể xóa món ăn');
      }
    }
  };

  const filteredFoods = foods.filter(food => {
    const matchesSearch = food.foodName?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesMealType = selectedMealType === 'ALL' || food.mealType === selectedMealType;
    return matchesSearch && matchesMealType;
  });

  const getMealTypeLabel = (mealType: string) => {
    const labels: Record<string, string> = {
      'BREAKFAST': 'Bữa sáng', 'LUNCH': 'Bữa trưa', 'DINNER': 'Bữa tối', 'SNACK': 'Ăn vặt'
    };
    return labels[mealType] || mealType;
  };

  const getDifficultyLabel = (level: string) => {
    const labels: Record<string, string> = { 'EASY': 'Dễ', 'MEDIUM': 'Trung bình', 'HARD': 'Khó' };
    return labels[level] || level;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-brand-500"></div>
      </div>
    );
  }

  return (
    <>
      <PageMeta title={`${t('foods.title')} | Health Care Admin`} description={t('foods.subtitle')} />
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">{t('foods.title')}</h1>
            <p className="text-gray-500 dark:text-gray-400">{t('foods.subtitle')}</p>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-500">Tổng: {foods.length} món</span>
          </div>
        </div>
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="relative flex-1">
            <svg className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input type="text" placeholder="Tìm kiếm món ăn..." className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-700 dark:bg-gray-800 dark:text-white" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
          </div>
          <select className="px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-700 dark:bg-gray-800 dark:text-white" value={selectedMealType} onChange={(e) => setSelectedMealType(e.target.value)}>
            <option value="ALL">Tất cả bữa ăn</option>
            <option value="BREAKFAST">Bữa sáng</option>
            <option value="LUNCH">Bữa trưa</option>
            <option value="DINNER">Bữa tối</option>
            <option value="SNACK">Ăn vặt</option>
          </select>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredFoods.map((food) => (
            <div key={food.foodId} className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03] hover:shadow-lg transition-shadow">
              <img className="w-full h-48 object-cover rounded-lg mb-4" src={food.imageUrl || 'https://via.placeholder.com/300x200'} alt={food.foodName} onError={(e) => { (e.target as HTMLImageElement).src = 'https://via.placeholder.com/300x200'; }} />
              <div className="space-y-3">
                <div className="flex justify-between items-start">
                  <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{food.foodName}</h3>
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${food.mealType === 'BREAKFAST' ? 'bg-yellow-100 text-yellow-800' : food.mealType === 'LUNCH' ? 'bg-blue-100 text-blue-800' : food.mealType === 'DINNER' ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'}`}>{getMealTypeLabel(food.mealType)}</span>
                </div>
                {food.categoryResponse && <div className="text-xs text-gray-500">Danh mục: {food.categoryResponse.categoryName}</div>}
                <div className="grid grid-cols-2 gap-2 text-sm">
                  <div className="text-gray-500"><span className="font-medium text-gray-800 dark:text-white">{food.caloriesPer100g}</span> kcal</div>
                  <div className="text-gray-500"><span className="font-medium text-gray-800 dark:text-white">{food.proteinPer100g}g</span> protein</div>
                  <div className="text-gray-500"><span className="font-medium text-gray-800 dark:text-white">{food.fatPer100g}g</span> fat</div>
                  <div className="text-gray-500"><span className="font-medium text-gray-800 dark:text-white">{food.fiberPer100g}g</span> fiber</div>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${food.difficultyLevel === 'EASY' ? 'bg-green-100 text-green-800' : food.difficultyLevel === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'}`}>{getDifficultyLabel(food.difficultyLevel)}</span>
                  <button onClick={() => handleDelete(food.foodId)} className="text-red-600 hover:text-red-800 p-1">
                    <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
        {filteredFoods.length === 0 && !loading && (
          <div className="text-center py-12">
            <h3 className="text-sm font-medium text-gray-900 dark:text-white">Không tìm thấy món ăn</h3>
          </div>
        )}
      </div>
    </>
  );
}
