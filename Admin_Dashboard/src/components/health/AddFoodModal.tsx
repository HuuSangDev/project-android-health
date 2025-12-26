import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { foodService } from "../../services/foodService";
import { FoodCategory } from "../../types/health";
import toast from "react-hot-toast";

interface AddFoodModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function AddFoodModal({
  isOpen,
  onClose,
  onSuccess,
}: AddFoodModalProps) {
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<FoodCategory[]>([]);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [imageFile, setImageFile] = useState<File | null>(null);

  const [formData, setFormData] = useState({
    foodName: "",
    caloriesPer100g: "",
    proteinPer100g: "",
    fatPer100g: "",
    fiberPer100g: "",
    sugarPer100g: "",
    instructions: "",
    prepTime: "",
    cookTime: "",
    servings: "",
    mealType: "BREAKFAST",
    difficultyLevel: "EASY",
    categoryId: "",
    goal: "WEIGHT_LOSS",
  });

  useEffect(() => {
    if (isOpen) loadCategories();
  }, [isOpen]);

  const loadCategories = async () => {
    try {
      const data = await foodService.getAllCategories();
      setCategories(data);
    } catch (error) {
      console.error("Failed to load categories:", error);
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => setImagePreview(reader.result as string);
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.foodName.trim()) {
      toast.error("Vui lòng nhập tên món ăn");
      return;
    }
    if (!formData.caloriesPer100g) {
      toast.error("Vui lòng nhập calories");
      return;
    }
    setLoading(true);

    try {
      const submitData = new FormData();
      submitData.append("foodName", formData.foodName.trim());
      submitData.append("caloriesPer100g", formData.caloriesPer100g);
      submitData.append("goal", formData.goal);
      if (formData.proteinPer100g)
        submitData.append("proteinPer100g", formData.proteinPer100g);
      if (formData.fatPer100g)
        submitData.append("fatPer100g", formData.fatPer100g);
      if (formData.fiberPer100g)
        submitData.append("fiberPer100g", formData.fiberPer100g);
      if (formData.sugarPer100g)
        submitData.append("sugarPer100g", formData.sugarPer100g);
      if (formData.instructions)
        submitData.append("instructions", formData.instructions.trim());
      if (formData.prepTime) submitData.append("prepTime", formData.prepTime);
      if (formData.cookTime) submitData.append("cookTime", formData.cookTime);
      if (formData.servings) submitData.append("servings", formData.servings);
      if (formData.mealType) submitData.append("mealType", formData.mealType);
      if (formData.difficultyLevel)
        submitData.append("difficultyLevel", formData.difficultyLevel);
      if (formData.categoryId)
        submitData.append("categoryId", formData.categoryId);
      if (imageFile) submitData.append("image", imageFile);

      await foodService.createFood(submitData);
      toast.success("Thêm món ăn thành công!");
      onSuccess();
      handleClose();
    } catch (error) {
      console.error("Error creating food:", error);
      toast.error("Có lỗi xảy ra khi thêm món ăn");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      foodName: "",
      caloriesPer100g: "",
      proteinPer100g: "",
      fatPer100g: "",
      fiberPer100g: "",
      sugarPer100g: "",
      instructions: "",
      prepTime: "",
      cookTime: "",
      servings: "",
      mealType: "BREAKFAST",
      difficultyLevel: "EASY",
      categoryId: "",
      goal: "WEIGHT_LOSS",
    });
    setImageFile(null);
    setImagePreview(null);
    onClose();
  };

  if (!isOpen) return null;

  const modalContent = (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[999999] p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-xl">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-800 dark:text-white">
              Thêm món ăn mới
            </h2>
            <button
              onClick={handleClose}
              className="text-gray-400 hover:text-gray-600"
            >
              <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Tên món ăn *
                </label>
                <input
                  type="text"
                  name="foodName"
                  value={formData.foodName}
                  onChange={handleInputChange}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Danh mục
                </label>
                <select
                  name="categoryId"
                  value={formData.categoryId}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                >
                  <option value="">Chọn danh mục</option>
                  {categories.map((cat) => (
                    <option key={cat.categoryId} value={cat.categoryId}>
                      {cat.categoryName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Calories/100g *
                </label>
                <input
                  type="number"
                  name="caloriesPer100g"
                  value={formData.caloriesPer100g}
                  onChange={handleInputChange}
                  required
                  min="0"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Protein/100g
                </label>
                <input
                  type="number"
                  name="proteinPer100g"
                  value={formData.proteinPer100g}
                  onChange={handleInputChange}
                  min="0"
                  step="0.1"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Fat/100g
                </label>
                <input
                  type="number"
                  name="fatPer100g"
                  value={formData.fatPer100g}
                  onChange={handleInputChange}
                  min="0"
                  step="0.1"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Fiber/100g
                </label>
                <input
                  type="number"
                  name="fiberPer100g"
                  value={formData.fiberPer100g}
                  onChange={handleInputChange}
                  min="0"
                  step="0.1"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Loại bữa ăn
                </label>
                <select
                  name="mealType"
                  value={formData.mealType}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                >
                  <option value="BREAKFAST">Bữa sáng</option>
                  <option value="LUNCH">Bữa trưa</option>
                  <option value="DINNER">Bữa tối</option>
                  <option value="SNACK">Ăn vặt</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Độ khó
                </label>
                <select
                  name="difficultyLevel"
                  value={formData.difficultyLevel}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                >
                  <option value="EASY">Dễ</option>
                  <option value="MEDIUM">Trung bình</option>
                  <option value="HARD">Khó</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Mục tiêu *
                </label>
                <select
                  name="goal"
                  value={formData.goal}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                >
                  <option value="WEIGHT_LOSS">Giảm cân</option>
                  <option value="MAINTAIN">Duy trì</option>
                  <option value="WEIGHT_GAIN">Tăng cân</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Hướng dẫn nấu
              </label>
              <textarea
                name="instructions"
                value={formData.instructions}
                onChange={handleInputChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Hình ảnh
              </label>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              />
              {imagePreview && (
                <img
                  src={imagePreview}
                  alt="Preview"
                  className="mt-2 w-32 h-32 object-cover rounded-md"
                />
              )}
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <button
                type="button"
                onClick={handleClose}
                className="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50 dark:text-gray-300 dark:border-gray-600"
              >
                Hủy
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? "Đang thêm..." : "Thêm món ăn"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
}
