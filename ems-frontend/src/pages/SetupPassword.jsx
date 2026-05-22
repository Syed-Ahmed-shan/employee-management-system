import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { changePassword } from '../api/api';
import { useAuth } from '../context/AuthContext';

export default function SetupPassword() {
  const [form, setForm] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { updateFirstLogin } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (form.newPassword !== form.confirmPassword) {
      setError('New passwords do not match!');
      return;
    }

    if (form.newPassword.length < 6) {
      setError('New password must be at least 6 characters long.');
      return;
    }

    setLoading(true);
    try {
      await changePassword({
        oldPassword: form.oldPassword,
        newPassword: form.newPassword
      });
      
      // Update context and redirect to dashboard
      updateFirstLogin(false);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password. Please check your current password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-bg-orb orb-1" />
      <div className="login-bg-orb orb-2" />

      <div className="login-card fade-in" style={{ maxWidth: '450px' }}>
        <div className="login-logo">🔒</div>
        <h1 className="login-title">Secure Your Account</h1>
        <p className="login-subtitle">For your security, please change your default password to continue.</p>

        {error && (
          <div className="alert alert-error mt-16">
            ⚠️ {error}
          </div>
        )}

        <form className="login-form mt-24" onSubmit={handleSubmit}>
          <div className="form-group mb-16">
            <label className="form-label">Current Password</label>
            <input
              className="form-input"
              type="password"
              placeholder="e.g. Welcome@123"
              value={form.oldPassword}
              onChange={(e) => setForm({ ...form, oldPassword: e.target.value })}
              required
            />
          </div>

          <div className="form-group mb-16">
            <label className="form-label">New Password</label>
            <input
              className="form-input"
              type="password"
              placeholder="Min 6 characters"
              value={form.newPassword}
              onChange={(e) => setForm({ ...form, newPassword: e.target.value })}
              required
            />
          </div>

          <div className="form-group mb-24">
            <label className="form-label">Confirm New Password</label>
            <input
              className="form-input"
              type="password"
              placeholder="Re-type new password"
              value={form.confirmPassword}
              onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
              required
            />
          </div>

          <button className="login-btn" type="submit" disabled={loading}>
            {loading ? '⏳ Updating...' : 'Update Password & Continue'}
          </button>
        </form>
      </div>
    </div>
  );
}
