import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import Navbar from "./components/layout/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import AuthCallback from "./pages/AuthCallback";
import Dashboard from "./pages/Dashboard";
import UserProfile from "./pages/UserProfile";
import CareerBlueprint from "./pages/students/CareerBlueprint";
import EducationList from "./pages/students/education/EducationList";
import RoleList from "./pages/students/RoleList";
import IndustrySelection from "./pages/students/industry/IndustrySelection";
import RoleOrEducation from "./pages/students/industry/RoleOrEducation";
import EducationSelection from "./pages/students/industry/EducationSelection";
import SpecializationSelection from "./pages/students/education/SpecializationSelection";
import RoleSelection from "./pages/students/RoleSelection";
import JobDescription from "./pages/students/JobDescription";
import PreparationAnalytics from "./pages/students/PreparationAnalytics";
import SkillTest from "./pages/students/SkillTest";
import ResumeBuilder from "./pages/students/ResumeBuilder";
import ResumeAccess from "./pages/industry/ResumeAccess";
import AdminDashboard from "./pages/AdminDashboard";
import ResumeReview from "./pages/admin/ResumeReview";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/auth/callback" element={<AuthCallback />} />
          
          {/* Protected Routes */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/profile" element={
            <ProtectedRoute>
              <UserProfile />
            </ProtectedRoute>
          } />
          
          {/* Students */}
          <Route path="/students/career-blueprint" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <CareerBlueprint />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/education" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <EducationList />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/role" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <RoleList />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/industry" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <IndustrySelection />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/industry/:industryName" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <RoleOrEducation />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/industry/:industryName/roles" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <RoleSelection />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/industry/:industryName/education" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <EducationSelection />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/education/:educationName" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <SpecializationSelection />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/specialization/:specializationName/roles" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <RoleSelection />
            </ProtectedRoute>
          } />
          <Route path="/students/career-blueprint/role/:roleName" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <JobDescription />
            </ProtectedRoute>
          } />
          <Route path="/students/preparation-analytics/:roleName" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <PreparationAnalytics />
            </ProtectedRoute>
          } />
          <Route path="/students/skill-test/:roleName/:skillName" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <SkillTest />
            </ProtectedRoute>
          } />
          <Route path="/students/resume-builder" element={
            <ProtectedRoute requiredUserType="STUDENT">
              <ResumeBuilder />
            </ProtectedRoute>
          } />
          
          {/* Industry */}
          <Route path="/industry/resume-access" element={
            <ProtectedRoute requiredUserType="INDUSTRY">
              <ResumeAccess />
            </ProtectedRoute>
          } />
          
          {/* Admin */}
          <Route path="/admin/dashboard" element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          } />
          <Route path="/admin/resume-review" element={
            <ProtectedRoute requiredRole="ADMIN">
              <ResumeReview />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;