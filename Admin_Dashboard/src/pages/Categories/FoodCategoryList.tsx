import { useState, useEffect } from 'react';
import { foodService } from '../../services/foodService';
import { FoodCategory } from '../../types/health';
import PageMeta from '../../components/common/PageMeta';
import toast from 'react-hot-toast';

export default function FoodCategoryList() {
  const [categories, setCategories] = useState<FoodCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState<FoodCategory | null>(null);
  const [formData, setFormData] = useState({ categoryName: '', description: '' });

  useEffect(() => { loadCategories(); }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const data = await foodService.getAllCategories();
      setCategories(data);
    } catch (error) {
      toast.error('Không thể tải danh mục');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingCategory) {
        await foodService.updateCategory(editingCategory.categoryId, formData);
        toast.success('Cập nhật thành công');
      } else {
        await foodService.createCategory(formData);
        toast.success('Tạo danh mục thành công');
      }
      setShowModal(false);
      setFormData({ categoryName: '', description: '' });
      setEditingCategory(null);
      loadCategories();
    } catch (error) {
      toast.error('Có lỗi xảy ra');
    }
  };

  const handleEdit = (category: FoodCategory) => {
    setEditingCategory(category);
    setFormData({ categoryName: category.categoryName, description: category.description || '' });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Bạn có chắc muốn xóa danh mục này?')) {
      try {
        await foodService.deleteCategory(id);
        toast.success('Xóa thành công');
        loadCategories();
      } catch (error) {
        toast.error('Không thể xóa danh mục');
      }
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64"><div className="animate-spin rounded-full h-16 w-16 border-b-2 border-brand-500"></div></div>;
  }

  return (
    <>
      <PageMeta title="Danh mục món ăn | Health Care Admin" description="Quản lý danh mục món ăn" />
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 dark:text-white">Danh mục món ăn</h1>
            <p className="text-gray-500">Quản lý các danh mục thực phẩm</p>
          </div>
          <button onClick={() => { setEditingCategory(null); setFormData({ categoryName: '', description: '' }); setShowModal(true); }} className="bg-brand-500 text-white px-4 py-2 rounded-lg hover:bg-brand-600">
            + Thêm danh mục
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {categories.map((cat) => (
            <div key={cat.categoryId} className="rounded-xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
              <h3 className="text-lg font-semibold text-gray-800 dark:text-white">{cat.categoryName}</h3>
              <p className="text-sm text-gray-500 mt-1">{cat.description || 'Không có mô tả'}</p>
              <div className="flex gap-2 mt-4">
                <button onClick={() => handleEdit(cat)} className="text-brand-500 hover:text-brand-600 text-sm">Sửa</button>
                <button onClick={() => handleDelete(cat.categoryId)} className="text-red-500 hover:text-red-600 text-sm">Xóa</button>
              </div>
            </div>
          ))}
        </div>

        {categories.length === 0 && <div className="text-center py-12 text-gray-500">Chưa có danh mục nào</div>}

        {showModal && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div className="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-md">
              <h2 className="text-xl font-bold mb-4 dark:text-white">{editingCategory ? 'Sửa danh mục' : 'Thêm danh mục'}</h2>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Tên danh mục</label>
                  <input type="text" value={formData.categoryName} onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })} className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white" required />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Mô tả</label>
                  <textarea value={formData.description} onChange={(e) => setFormData({ ...formData, description: e.target.value })} className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:border-gray-600 dark:text-white" rows={3} />
                </div>
                <div className="flex gap-2 justify-end">
                  <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2 text-gray-600 hover:text-gray-800">Hủy</button>
                  <button type="submit" className="px-4 py-2 bg-brand-500 text-white rounded-lg hover:bg-brand-600">Lưu</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
