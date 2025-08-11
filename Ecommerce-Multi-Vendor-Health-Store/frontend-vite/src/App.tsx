import * as React from "react"
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { motion, AnimatePresence } from "framer-motion"
import { Toaster } from "sonner"
import { Navigation } from "@/components/layout/Navigation"
import { HomePage } from "@/pages/HomePage"
import { ProductsPage } from "@/pages/ProductsPage"
import { ProductDetailPage } from "@/pages/ProductDetailPage"
import { CartPage } from "@/pages/CartPage"
import { CheckoutPage } from "@/pages/CheckoutPage"
import { LoginPage } from "@/pages/auth/LoginPage"
import { RegisterPage } from "@/pages/auth/RegisterPage"
import { ProfilePage } from "@/pages/user/ProfilePage"
import { DashboardPage } from "@/pages/admin/DashboardPage"
import { NotFoundPage } from "@/pages/NotFoundPage"
import { Footer } from "@/components/layout/Footer"
import { Chatbot } from "@/components/chatbot/Chatbot"

// Page transition variants
const pageVariants = {
  initial: {
    opacity: 0,
    y: 20,
  },
  in: {
    opacity: 1,
    y: 0,
  },
  out: {
    opacity: 0,
    y: -20,
  },
}

const pageTransition = {
  type: "tween",
  ease: "anticipate",
  duration: 0.4,
}

// Layout wrapper for pages
function PageLayout({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial="initial"
      animate="in"
      exit="out"
      variants={pageVariants}
      transition={pageTransition}
      className="min-h-screen flex flex-col"
    >
      <Navigation />
      <main className="flex-1">
        {children}
      </main>
      <Footer />
      <Chatbot />
    </motion.div>
  )
}

// Protected route wrapper
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  // TODO: Add authentication check
  const isAuthenticated = false // Replace with actual auth state
  
  if (!isAuthenticated) {
    return <LoginPage />
  }
  
  return <>{children}</>
}

// Admin route wrapper
function AdminRoute({ children }: { children: React.ReactNode }) {
  // TODO: Add admin role check
  const isAdmin = false // Replace with actual admin check
  
  if (!isAdmin) {
    return <NotFoundPage />
  }
  
  return <>{children}</>
}

function App() {
  return (
    <Router>
      <div className="App">
        <AnimatePresence mode="wait">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={
              <PageLayout>
                <HomePage />
              </PageLayout>
            } />
            
            <Route path="/products" element={
              <PageLayout>
                <ProductsPage />
              </PageLayout>
            } />
            
            <Route path="/products/:id" element={
              <PageLayout>
                <ProductDetailPage />
              </PageLayout>
            } />
            
            <Route path="/cart" element={
              <PageLayout>
                <CartPage />
              </PageLayout>
            } />
            
            {/* Auth Routes */}
            <Route path="/login" element={
              <PageLayout>
                <LoginPage />
              </PageLayout>
            } />
            
            <Route path="/register" element={
              <PageLayout>
                <RegisterPage />
              </PageLayout>
            } />
            
            {/* Protected Routes */}
            <Route path="/checkout" element={
              <PageLayout>
                <ProtectedRoute>
                  <CheckoutPage />
                </ProtectedRoute>
              </PageLayout>
            } />
            
            <Route path="/profile" element={
              <PageLayout>
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              </PageLayout>
            } />
            
            {/* Admin Routes */}
            <Route path="/admin/*" element={
              <PageLayout>
                <ProtectedRoute>
                  <AdminRoute>
                    <DashboardPage />
                  </AdminRoute>
                </ProtectedRoute>
              </PageLayout>
            } />
            
            {/* 404 Route */}
            <Route path="*" element={
              <PageLayout>
                <NotFoundPage />
              </PageLayout>
            } />
          </Routes>
        </AnimatePresence>
        
        {/* Global Toast Notifications */}
        <Toaster 
          position="bottom-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: 'hsl(var(--background))',
              color: 'hsl(var(--foreground))',
              border: '1px solid hsl(var(--border))',
            },
          }}
        />
      </div>
    </Router>
  )
}

export default App
