import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { exerciseService } from "../../services/exerciseService";
import { Exercise, ExerciseCategory } from "../../types/health";
import toast from "react-hot-toast";

interface EditExerciseModalProps {
  isOpen: boolean;
  exercise: Exercise | null;
  onClose: () => void;
  onSuccess: () => void;
}

export default function EditExerciseModal({
  isOpen,
  exercise,
  onClose,
  onSuccess,
}: EditExerciseModalProps) {
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<ExerciseCategory[]>([]);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [videoFile, setVideoFile] = useState<File | null>(null);

  const [formData, setFormData] = useState({
    exerciseName: "",
    caloriesPerMinute: "",
    description: "",
    instructions: "",
    difficultyLevel: "BEGINNER",
    categoryId: "",
    goal: "WEIGHT_LOSS",
  });

  useEffect(() => {
    if (isOpen && exercise) {
      setFormData({
        exerciseName: exercise.exerciseName || "",
        caloriesPerMinute: String(exercise.caloriesPerMinute || ""),
        description: exercise.description || "",
        instructions: exercise.instructions || "",
        difficultyLevel: exercise.difficultyLevel || "BEGINNER",
        categoryId: String(exercise.category?.categoryId || ""),
        goal: exercise.goal || "WEIGHT_LOSS",
      });
      setImagePreview(exercise.imageUrl || null);
      loadCategories();
    }
  }, [isOpen, exercise]);

  const loadCategories = async () => {
    try {
      const data = await exerciseService.getAllCategories();
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

  const handleVideoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) setVideoFile(file);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!exercise) return;
    setLoading(true);

    try {
      const submitData = new FormData();
      submitData.append("goal", formData.goal);
      if (formData.exerciseName)
        submitData.append("exerciseName", formData.exerciseName.trim());
      if (formData.caloriesPerMinute)
        submitData.append("caloriesPerMinute", formData.caloriesPerMinute);
      if (formData.description)
        submitData.append("description", formData.description.trim());
      if (formData.instructions)
        submitData.append("instructions", formData.instructions.trim());
      if (formData.difficultyLevel)
        submitData.append("difficultyLevel", formData.difficultyLevel);
      if (formData.categoryId)
        submitData.append("categoryId", formData.categoryId);
      if (imageFile) submitData.append("image", imageFile);
      if (videoFile) submitData.append("video", videoFile);

      await exerciseService.updateExercise(exercise.exerciseId, submitData);
      toast.success("Cập nhật bài tập thành công!");
      onSuccess();
      onClose();
    } catch (error) {
      console.error("Error updating exercise:", error);
      toast.error("Có lỗi xảy ra khi cập nhật bài tập");
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !exercise) return null;

  const modalContent = (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[999999] p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-xl">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-800 dark:text-white">
              Sửa bài tập
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
                  Tên bài tập
                </label>
                <input
                  type="text"
                  name="exerciseName"
                  value={formData.exerciseName}
                  onChange={handleInputChange}
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
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Calories/phút
                </label>
                <input
                  type="number"
                  name="caloriesPerMinute"
                  value={formData.caloriesPerMinute}
                  onChange={handleInputChange}
                  min="0"
                  step="0.1"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
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
                  <option value="BEGINNER">Cơ bản</option>
                  <option value="INTERMEDIATE">Trung bình</option>
                  <option value="ADVANCED">Nâng cao</option>
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
                Mô tả
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Hướng dẫn
              </label>
              <textarea
                name="instructions"
                value={formData.instructions}
                onChange={handleInputChange}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              />
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Video
                </label>
                <input
                  type="file"
                  accept="video/*"
                  onChange={handleVideoChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                />
                {videoFile && (
                  <p className="mt-2 text-sm text-gray-600">
                    Đã chọn: {videoFile.name}
                  </p>
                )}
              </div>
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
