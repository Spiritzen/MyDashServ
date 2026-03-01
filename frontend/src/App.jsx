// App.jsx
import { Routes, Route } from "react-router-dom";
import { AuthProvider } from "@/context/authContext";
import PrivateRoute from "@/routes/PrivateRoute";
import Login from "@/pages/Login";
import AdminLayout from "@/admin/AdminLayout";
import FormateurLayout from "@/formateur/FormateurLayout";
import Home from "@/pages/Home";
import RegisterFormateur from "@/pages/RegisterFormateur";
import VerifyEmailResult from "@/public/VerifyEmailResult";

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* publiques */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<RegisterFormateur />} />
        <Route path="/verify-email" element={<VerifyEmailResult />} />

        {/* admin protégées */}
        <Route element={<PrivateRoute requireAdmin />}>
          <Route path="/admin" element={<AdminLayout />} />
          <Route path="/admin/sessions" element={<AdminLayout page="sessions" />} />
          <Route path="/admin/formations" element={<AdminLayout page="formations" />} />
          {/* ✅ AJOUT ICI */}
          <Route path="/admin/formateurs" element={<AdminLayout page="formateurs" />} />
          <Route path="/admin/affectations" element={<AdminLayout page="affectations" />} />
        </Route>

        {/* formateur protégées */}
        <Route element={<PrivateRoute />}>
          <Route path="/formateur" element={<FormateurLayout />} />
          <Route path="/formateur/profil" element={<FormateurLayout page="profil" />} />
          <Route path="/formateur/competences" element={<FormateurLayout page="competences" />} />
          <Route path="/formateur/dispos-conges" element={<FormateurLayout page="dispos" />} />
          <Route path="/formateur/formations" element={<FormateurLayout page="formations" />} />
          <Route path="/formateur/candidature" element={<FormateurLayout page="candidature" />} />
          <Route path="/formateur/documents" element={<FormateurLayout page="documents" />} />
          <Route path="/formateur/securite" element={<FormateurLayout page="securite" />} />
        </Route>

        {/* fallback */}
        <Route path="*" element={<Home />} />
      </Routes>
    </AuthProvider>
  );
}
