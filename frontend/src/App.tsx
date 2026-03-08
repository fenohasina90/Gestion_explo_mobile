import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { PrivateRoute } from './components/PrivateRoute';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { EnfantsPage } from './pages/EnfantsPage';
import { ActivitesPage } from './pages/ActivitesPage';
import { MonProfilPage } from './pages/MonProfilPage';
import { UtilisateursPage } from './pages/UtilisateursPage';
import { AnneesExercicePage } from './pages/AnneesExercicePage';
import { JournalPage } from './pages/JournalPage';
import { StaffsPage } from './pages/StaffsPage';
import { BudgetPage } from './pages/BudgetPage';
import { CategoriesProgrammePage } from './pages/CategoriesProgrammePage';
import { ProgrammesPage } from './pages/ProgrammesPage';
import { ClassesProgressivesPage } from './pages/ClassesProgressivesPage';
import { CPDetailsPage } from './pages/CPDetailsPage';
import { HistoriqueProgrammesPage } from './pages/HistoriqueProgrammesPage';
import { InscriptionsPage, RapportsPage } from './pages/PlaceholderPages';

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
                  <StaffsPage />
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
          <Route
            path="/mon-profil"
            element={
              <PrivateRoute>
                <Layout>
                  <MonProfilPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/utilisateurs"
            element={
              <PrivateRoute>
                <Layout>
                  <UtilisateursPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/annees-exercice"
            element={
              <PrivateRoute>
                <Layout>
                  <AnneesExercicePage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/journal"
            element={
              <PrivateRoute>
                <Layout>
                  <JournalPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/categories-programme"
            element={
              <PrivateRoute>
                <Layout>
                  <CategoriesProgrammePage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/programmes"
            element={
              <PrivateRoute>
                <Layout>
                  <ProgrammesPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/cp"
            element={
              <PrivateRoute>
                <Layout>
                  <ClassesProgressivesPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/cp/:cpId/programmes"
            element={
              <PrivateRoute>
                <Layout>
                  <CPDetailsPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/historique-programmes"
            element={
              <PrivateRoute>
                <Layout>
                  <HistoriqueProgrammesPage />
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
