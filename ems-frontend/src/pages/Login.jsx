import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/api';
import { useAuth } from '../context/AuthContext';
import DemoGuideModal from '../components/DemoGuideModal';

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isGuideOpen, setIsGuideOpen] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  // Auto-open modal on first load if not seen before
  useEffect(() => {
    const hasSeenGuide = localStorage.getItem('hasSeenDemoGuide');
    if (!hasSeenGuide) {
      setIsGuideOpen(true);
      localStorage.setItem('hasSeenDemoGuide', 'true');
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(form);
      loginUser(res.data.data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      {/* Floating Info Button */}
      <button className="floating-info-btn" onClick={() => setIsGuideOpen(true)}>
        <span className="info-icon">i</span> Instructions
      </button>

      <div className="login-bg-orb orb-1" />
      <div className="login-bg-orb orb-2" />

      <div className="login-card fade-in">
        <div className="login-logo">⚡</div>
        <h1 className="login-title">Welcome Back</h1>
        <p className="login-subtitle">Sign in to EMS Portal to continue</p>

        {error && (
          <div className="alert alert-error mt-16">
            ⚠️ {error}
          </div>
        )}

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Employee ID / Email</label>
            <input
              className="form-input"
              type="text"
              placeholder="e.g. DEV-A7K29 or admin@company.com"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              className="form-input"
              type="password"
              placeholder="Enter your password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </div>

          <button className="login-btn" type="submit" disabled={loading}>
            {loading ? '⏳ Signing in...' : '🔐 Sign In'}
          </button>
        </form>

        <p className="text-muted text-sm mt-24" style={{ textAlign: 'center' }}>
          Please contact your administrator to create an account.
        </p>
      </div>

      {/* The Demo Guide Modal */}
      <DemoGuideModal isOpen={isGuideOpen} onClose={() => setIsGuideOpen(false)} />
    </div>
  );
}
