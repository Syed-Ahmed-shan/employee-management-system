import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useEffect, useState } from 'react';
import { getUnreadNotifications, markNotificationAsRead } from '../api/api';

const navItems = [
  { to: '/dashboard', icon: '📊', label: 'Dashboard' },
  { to: '/employees', icon: '👥', label: 'Employees' },
  { to: '/worklogs', icon: '📋', label: 'Work Logs' },
  { to: '/reports', icon: '📈', label: 'Reports' },
];

export default function Sidebar() {
  const { user, logoutUser } = useAuth();
  const navigate = useNavigate();
  
  const [notifications, setNotifications] = useState([]);
  const [showNotifs, setShowNotifs] = useState(false);

  const isEmployee = user?.role === 'ROLE_EMPLOYEE';
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isManager = user?.role === 'ROLE_MANAGER';

  useEffect(() => {
    if (user && (user.role === 'ROLE_ADMIN' || user.role === 'ROLE_MANAGER')) {
      getUnreadNotifications().then(res => {
        setNotifications(res.data.data || []);
      }).catch(console.error);
    }
  }, [user]);

  const handleRead = async (id) => {
    try {
      await markNotificationAsRead(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  const handleLogout = () => {
    logoutUser();
    navigate('/login');
  };

  const initials = user?.username?.slice(0, 2).toUpperCase() || 'U';

  const roleClass = {
    ROLE_ADMIN: 'badge-admin',
    ROLE_MANAGER: 'badge-manager',
    ROLE_EMPLOYEE: 'badge-employee',
  }[user?.role] || 'badge-employee';

  const roleLabel = user?.role?.replace('ROLE_', '') || 'USER';

  return (
    <aside className="sidebar">
      <div className="sidebar-brand" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2>⚡ EMS Portal</h2>
          <p>Employee Management</p>
        </div>
        {(user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_MANAGER') && (
          <div style={{ position: 'relative' }}>
            <button 
              className="btn btn-secondary btn-sm" 
              style={{ padding: '6px', borderRadius: '50%', background: 'transparent', border: 'none', position: 'relative' }}
              onClick={() => setShowNotifs(!showNotifs)}
            >
              <span style={{ fontSize: '1.2rem' }}>🔔</span>
              {notifications.length > 0 && (
                <span style={{ 
                  position: 'absolute', top: 0, right: 0, background: 'var(--accent-red)', 
                  color: 'white', fontSize: '0.6rem', padding: '2px 5px', borderRadius: '10px', fontWeight: 'bold' 
                }}>
                  {notifications.length}
                </span>
              )}
            </button>
            
            {showNotifs && (
              <div style={{
                position: 'absolute', top: '40px', left: '100%', marginLeft: '10px',
                width: '320px', background: 'var(--bg-secondary)', border: '1px solid var(--border)',
                borderRadius: '8px', boxShadow: 'var(--shadow-card)', zIndex: 1000, overflow: 'hidden'
              }}>
                <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)', fontWeight: 'bold', background: 'var(--bg-card)' }}>
                  Notifications ({notifications.length})
                </div>
                <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
                  {notifications.length === 0 ? (
                    <div style={{ padding: '20px', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.85rem' }}>No new notifications</div>
                  ) : (
                    notifications.map(n => (
                      <div key={n.id} style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)' }}>
                        <div style={{ fontSize: '0.85rem', fontWeight: 'bold', marginBottom: '4px' }}>{n.title}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginBottom: '8px' }}>{n.message}</div>
                        <button className="btn btn-secondary" style={{ fontSize: '0.7rem', padding: '4px 8px' }} onClick={() => handleRead(n.id)}>
                          Mark as read
                        </button>
                      </div>
                    ))
                  )}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className="nav-link">
          <span className="nav-icon">📊</span>
          Dashboard
        </NavLink>

        {isEmployee ? (
          <></>
        ) : (
          <>
            <NavLink to="/employees" className="nav-link">
              <span className="nav-icon">👥</span>
              {isAdmin ? 'Employees & Managers' : 'My Team'}
            </NavLink>

            <NavLink to="/worklogs" className="nav-link">
              <span className="nav-icon">📝</span>
              Task Management
            </NavLink>

            <NavLink to="/reports" className="nav-link">
              <span className="nav-icon">📈</span>
              Reports & Analytics
            </NavLink>
          </>
        )}
      </nav>

      <div className="sidebar-footer">
        <div className="user-card">
          <div className="user-avatar">{initials}</div>
          <div className="user-info">
            <div className="user-name">{user?.username}</div>
            <span className={`badge ${roleClass}`} style={{ fontSize: '0.6rem', padding: '1px 6px' }}>
              {roleLabel}
            </span>
          </div>
          <button className="logout-btn" onClick={handleLogout} title="Logout">
            🚪
          </button>
        </div>
      </div>
    </aside>
  );
}
