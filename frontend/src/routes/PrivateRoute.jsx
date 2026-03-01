// src/routes/PrivateRoute.jsx
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/context/authContext";

export default function PrivateRoute({ requireAdmin = false }) {
  const { user, isAdmin } = useAuth();

  if (!user) return <Navigate to="/" replace />;            // ← Home
  if (requireAdmin && !isAdmin) return <Navigate to="/" replace />;

  return <Outlet />;
}
