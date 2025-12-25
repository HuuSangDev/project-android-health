import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { exerciseService } from '../../services/exerciseService';
import { Exercise } from '../../types/health';
import PageMeta from '../../components/common/PageMeta';
import toast from 'react-hot-toast';

export default function ExerciseList() {
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('ALL');
  const { t } = useTranslation();

  useEffect(() => {
    loadExercises();
  }, []);

  const loadExercises = async () => {
    try {
      setLoading(true);
      const data = await exerciseService.getAllExercises();
      setExercises(data);
    } catch (error) {
      console.error('Failed to load exercises:', error);
      toast.error('Không thể tải danh sách bài tập');
      setExercises([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Bạn có chắc muốn xóa bài tập này?')) {
      try {
        await exerciseService.deleteExercise(id);
        setExercises(exercises.filter(ex => ex.exerciseId !== id));
        toast.success('Xóa bài tập thành công');
      } catch (error) {
        toast.error('Không thể xóa bài tập');
      }
    }
  };

  const filteredExercises = exercises.filter(exercise => {
    const matchesSearch = exercise.exerciseName?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDifficulty = selectedDifficulty === 'ALL' || exercise.difficultyLevel === selectedDifficulty;
    return matchesSearch && matchesDifficulty;
  });

  const getDifficultyLabel = (level: string) => {
    const labels: Record<string, string> = { 'BEGINNER': 'Cơ bản', 'INTERMEDIATE': 'Trung bình', 'ADVANCED': 'Nâng cao' };
    return labels[level] || level;
  };

  const getGoalLabel = (goal: string) => {
    const labels: Record<string, string> = { 'WEIGHT_LOSS': 'Giảm cân', 'MUSCLE_GAIN': 'Tăng cơ', 'MAINTAIN_WEIGHT': 'Duy trì' };
    return labels[goal] || goal;
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
      <PageMeta title={`${t('exercises.title')} | Health Care Admin`} description={t('exercises.subtitle')} />
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">{t('exercises.title')}</h1>
            <p className="text-gray-500 dark:text-gray-400">{t('exercises.subtitle')}</p>
          </div>
          <span className="text-sm text-gray-500">Tổng: {exercises.length} bài tập</span>
        </div>
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="relative flex-1">
            <svg className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input type="text" placeholder="Tìm kiếm bài tập..." className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-700 dark:bg-gray-800 dark:text-white" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
          </div>
          <select className="px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-brand-500 dark:border-gray-700 dark:bg-gray-800 dark:text-white" value={selectedDifficulty} onChange={(e) => setSelectedDifficulty(e.target.value)}>
            <option value="ALL">Tất cả độ khó</option>
            <option value="BEGINNER">Cơ bản</option>
            <option value="INTERMEDIATE">Trung bình</option>
            <option value="ADVANCED">Nâng cao</option>
          </select>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredExercises.map((exercise) => (
            <div key={exercise.exerciseId} className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03] hover:shadow-lg transition-shadow">
              <img className="w-full h-48 object-cover rounded-lg mb-4" src={exercise.imageUrl || 'https://via.placeholder.com/300x200'} alt={exercise.exerciseName} onError={(e) => { (e.target as HTMLImageElement).src = 'https://via.placeholder.com/300x200'; }} />
              <div className="space-y-3">
                <div className="flex justify-between items-start">
                  <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{exercise.exerciseName}</h3>
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${exercise.difficultyLevel === 'BEGINNER' ? 'bg-green-100 text-green-800' : exercise.difficultyLevel === 'INTERMEDIATE' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'}`}>{getDifficultyLabel(exercise.difficultyLevel)}</span>
                </div>
                <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">{exercise.description}</p>
                {exercise.category && <div className="text-xs text-gray-500">Danh mục: {exercise.category.categoryName}</div>}
                <div className="grid grid-cols-1 gap-2 text-sm">
                  <div className="text-gray-500"><span className="font-medium text-gray-800 dark:text-white">{exercise.caloriesPerMinute}</span> kcal/phút</div>
                  <div className="text-gray-500"><span className="font-medium">Nhóm cơ:</span> {exercise.muscleGroups || 'N/A'}</div>
                  <div className="text-gray-500"><span className="font-medium">Thiết bị:</span> {exercise.equipmentNeeded || 'Không cần'}</div>
                </div>
                {exercise.videoUrl && (
                  <a href={exercise.videoUrl} target="_blank" rel="noopener noreferrer" className="text-brand-500 hover:text-brand-600 text-sm flex items-center gap-1">
                    <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    Xem video
                  </a>
                )}
                <div className="flex justify-between items-center pt-2">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${exercise.goal === 'WEIGHT_LOSS' ? 'bg-blue-100 text-blue-800' : exercise.goal === 'MUSCLE_GAIN' ? 'bg-purple-100 text-purple-800' : 'bg-gray-100 text-gray-800'}`}>{getGoalLabel(exercise.goal)}</span>
                  <button onClick={() => handleDelete(exercise.exerciseId)} className="text-red-600 hover:text-red-800 p-1">
                    <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
        {filteredExercises.length === 0 && !loading && (
          <div className="text-center py-12">
            <h3 className="text-sm font-medium text-gray-900 dark:text-white">Không tìm thấy bài tập</h3>
          </div>
        )}
      </div>
    </>
  );
}
