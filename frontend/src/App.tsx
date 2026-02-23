import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { PrivateRoute } from './components/PrivateRoute';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { EnfantsPage } from './pages/EnfantsPage';
import { ActivitesPage } from './pages/ActivitesPage';
import { InscriptionsPage, StaffPage, BudgetPage, RapportsPage } from './pages/PlaceholderPages';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Route publique */}
          <Route path="/login" element={<LoginPage />} />

          {/* Routes protégées */}
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Layout>
                  <DashboardPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/enfants"
            element={
              <PrivateRoute>
                <Layout>
                  <EnfantsPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/activites"
            element={
              <PrivateRoute>
                <Layout>
                  <ActivitesPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/inscriptions"
            element={
              <PrivateRoute>
                <Layout>
                  <InscriptionsPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/staff"
            element={
              <PrivateRoute>
                <Layout>
                  <StaffPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/budget"
            element={
              <PrivateRoute>
                <Layout>
                  <BudgetPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/rapports"
            element={
              <PrivateRoute>
                <Layout>
                  <RapportsPage />
                </Layout>
              </PrivateRoute>
            }
          />

          {/* Redirection par défaut */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
