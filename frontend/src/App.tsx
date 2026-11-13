import { BrowserRouter, Navigate, Outlet, Route, Routes, useLocation } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth/AuthContext'
import CardDetailPage from './pages/CardDetailPage'
import CardsPage from './pages/CardsPage'
import LoginPage from './pages/LoginPage'

function ProtectedRoute() {
  const { usuario } = useAuth()
  const location = useLocation()

  if (!usuario) {
    const from = `${location.pathname}${location.search}`
    return <Navigate to="/login" replace state={{ from }} />
  }

  return <Outlet />
}

function HomeRedirect() {
  const { usuario } = useAuth()
  return <Navigate to={usuario ? '/cartas' : '/login'} replace />
}

export function AppRoutes() {
  return (
    <>
      <a className="skip-link" href="#conteudo">
        Pular para o conteúdo
      </a>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/cartas" element={<CardsPage />} />
          <Route path="/cartas/:id" element={<CardDetailPage />} />
        </Route>
        <Route path="*" element={<HomeRedirect />} />
      </Routes>
    </>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  )
}
