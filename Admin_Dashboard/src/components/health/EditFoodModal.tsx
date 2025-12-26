import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { foodService } from "../../services/foodService";
import { Food, FoodCategory } from "../../types/health";
import toast from "react-hot-toast";

interface EditFoodModalProps {
  isOpen: boolean;
  food: Food | null;
  onClose: () => void;
  onSuccess: () => void;
}

export default function EditFoodModal({
  isOpen,
  food,
  onClose,
  onSuccess,
}: EditFoodModalProps) {
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
    if (isOpen && food) {
      setFormData({
        foodName: food.foodName || "",
        caloriesPer100g: String(food.caloriesPer100g || ""),
        proteinPer100g: String(food.proteinPer100g || ""),
        fatPer100g: String(food.fatPer100g || ""),
        fiberPer100g: String(food.fiberPer100g || ""),
        sugarPer100g: String(food.sugarPer100g || ""),
        instructions: food.instructions || "",
        prepTime: String(food.prepTime || ""),
        cookTime: String(food.cookTime || ""),
        servings: String(food.servings || ""),
        mealType: food.mealType || "BREAKFAST",
        difficultyLevel: food.difficultyLevel || "EASY",
        categoryId: String(food.categoryResponse?.categoryId || ""),
        goal: food.goal || "WEIGHT_LOSS",
      });
      setImagePreview(food.imageUrl || null);
      loadCategories();
    }
  }, [isOpen, food]);

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
    if (!food) return;
    if (!formData.foodName.trim()) {
      toast.error("Vui lòng nhập tên món ăn");
      return;
    }
    setLoading(true);

    try {
      const submitData = new FormData();
      submitData.append("foodName", formData.foodName.trim());
      submitData.append("goal", formData.goal);
      if (formData.caloriesPer100g)
        submitData.append("caloriesPer100g", formData.caloriesPer100g);
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

      // Debug log
      console.log("Updating food ID:", food.foodId);
      console.log("Form data entries:");
      for (const [key, value] of submitData.entries()) {
        console.log(`  ${key}:`, value);
      }

      await foodService.updateFood(food.foodId, submitData);
      toast.success("Cập nhật món ăn thành công!");
      onSuccess();
      onClose();
    } catch (error: any) {
      console.error("Error updating food:", error);
      console.error("Response data:", error.response?.data);
      toast.error(
        error.response?.data?.message || "Có lỗi xảy ra khi cập nhật món ăn"
      );
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !food) return null;

  const modalContent = (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[999999] p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-xl">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-800 dark:text-white">
              Sửa món ăn
            </h2>
            <button
              onClick={onClose}
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
                  Calories/100g
                </label>
                <input
                  type="number"
                  name="caloriesPer100g"
                  value={formData.caloriesPer100g}
                  onChange={handleInputChange}
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
                  Mục tiêu
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
                onClick={onClose}
                className="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50 dark:text-gray-300 dark:border-gray-600"
              >
                Hủy
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? "Đang lưu..." : "Lưu thay đổi"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
}
