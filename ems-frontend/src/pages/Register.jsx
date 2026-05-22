import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../api/api';

export default function Register() {
  const [form, setForm] = useState({ username: '', password: '', role: 'ROLE_EMPLOYEE' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      setSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-bg-orb orb-1" />
      <div className="login-bg-orb orb-2" />

      <div className="login-card fade-in">
        <div className="login-logo">⚡</div>
        <h1 className="login-title">Create Account</h1>
        <p className="login-subtitle">Join the EMS Portal</p>

        {error && (
          <div className="alert alert-error mt-16">
            ⚠️ {error}
          </div>
        )}

        {success && (
          <div className="alert alert-success mt-16">
            ✅ Registration successful! Redirecting to login...
          </div>
        )}

        {!success && (
          <form className="login-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Username</label>
              <input
                className="form-input"
                type="text"
                placeholder="Choose a username"
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
                placeholder="Create a password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Role</label>
              <select
                className="form-select"
                value={form.role}
                onChange={(e) => setForm({ ...form, role: e.target.value })}
              >
                <option value="ROLE_EMPLOYEE">Employee</option>
                <option value="ROLE_MANAGER">Manager</option>
                <option value="ROLE_ADMIN">Admin</option>
              </select>
            </div>

            <button className="login-btn" type="submit" disabled={loading}>
              {loading ? '⏳ Registering...' : '📝 Register'}
            </button>
          </form>
        )}

        <p className="text-muted text-sm mt-24" style={{ textAlign: 'center' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--accent-blue)', textDecoration: 'underline' }}>Sign In</Link>
        </p>
      </div>
    </div>
  );
}
