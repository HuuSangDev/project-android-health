import { BrowserRouter as Router, Routes, Route } from "react-router";
import SignIn from "./pages/AuthPages/SignIn";
import SignUp from "./pages/AuthPages/SignUp";
import NotFound from "./pages/OtherPage/NotFound";
import AppLayout from "./layout/AppLayout";
import { ScrollToTop } from "./components/common/ScrollToTop";
import HealthDashboard from "./pages/Dashboard/HealthDashboard";
import FoodList from "./pages/Foods/FoodList";
import ExerciseList from "./pages/Exercises/ExerciseList";
import UserList from "./pages/Users/UserList";
import FoodCategoryList from "./pages/Categories/FoodCategoryList";
import ExerciseCategoryList from "./pages/Categories/ExerciseCategoryList";
import NotificationList from "./pages/Notifications/NotificationList";
import { AuthProvider } from "./context/AuthContext";
import { Toaster } from "react-hot-toast";
import ProtectedRoute from "./components/auth/ProtectedRoute";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <ScrollToTop />
        <Toaster position="top-right" />
        <Routes>
          <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
            <Route index path="/" element={<HealthDashboard />} />
            <Route path="/foods" element={<FoodList />} />
            <Route path="/exercises" element={<ExerciseList />} />
            <Route path="/users" element={<UserList />} />
            <Route path="/food-categories" element={<FoodCategoryList />} />
            <Route path="/exercise-categories" element={<ExerciseCategoryList />} />
            <Route path="/notifications" element={<NotificationList />} />
          </Route>
          <Route path="/signin" element={<SignIn />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
