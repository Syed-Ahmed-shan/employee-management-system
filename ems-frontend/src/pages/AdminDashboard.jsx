import { useEffect, useState } from 'react';
import { getEmployees, getWorkLogs, getAllReports } from '../api/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell, Legend
} from 'recharts';

const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#06b6d4'];

export default function AdminDashboard() {
  const [stats, setStats] = useState({ employees: 0, worklogs: 0, totalHours: 0, completed: 0 });
  const [hoursData, setHoursData] = useState([]);
  const [statusData, setStatusData] = useState([]);
  const [recentLogs, setRecentLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [empRes, wlRes, reportRes] = await Promise.all([
          getEmployees(), getWorkLogs(), getAllReports()
        ]);
        const employees = empRes.data.data || [];
        const worklogs = wlRes.data.data || [];
        const reports = reportRes.data.data || [];

        const totalHours = worklogs.reduce((s, w) => s + w.hoursWorked, 0);
        const completed = worklogs.filter(w => w.status === 'COMPLETED').length;

        setStats({ employees: employees.length, worklogs: worklogs.length, totalHours: totalHours.toFixed(1), completed });

        setHoursData(reports.map(r => ({
          name: r.employeeName.split(' ')[0],
          hours: r.totalHoursWorked,
          tasks: r.totalTasks,
        })));

        const pending = worklogs.filter(w => w.status === 'PENDING').length;
        const inProgress = worklogs.filter(w => w.status === 'IN_PROGRESS').length;
        setStatusData([
          { name: 'Completed', value: completed },
          { name: 'In Progress', value: inProgress },
          { name: 'Pending', value: pending },
        ]);

        setRecentLogs(worklogs.slice(0, 5));
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  const statusBadge = (status) => {
    const map = { COMPLETED: 'badge-completed', IN_PROGRESS: 'badge-progress', PENDING: 'badge-pending' };
    const label = { COMPLETED: 'Completed', IN_PROGRESS: 'In Progress', PENDING: 'Pending' };
    return <span className={`badge ${map[status] || ''}`}>{label[status] || status}</span>;
  };

  if (loading) return (
    <div className="loading-state">
      <div className="spinner" />
      <span>Loading dashboard...</span>
    </div>
  );

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">Admin <span>Dashboard</span></h1>
          <p className="page-subtitle">Company-wide overview.</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon blue">👥</div>
          <div>
            <div className="stat-label">Total Employees</div>
            <div className="stat-value">{stats.employees}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon purple">📋</div>
          <div>
            <div className="stat-label">Total Work Logs</div>
            <div className="stat-value">{stats.worklogs}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon green">⏱️</div>
          <div>
            <div className="stat-label">Total Hours Logged</div>
            <div className="stat-value">{stats.totalHours}h</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon orange">✅</div>
          <div>
            <div className="stat-label">Tasks Completed</div>
            <div className="stat-value">{stats.completed}</div>
          </div>
        </div>
      </div>

      <div className="grid-2 mb-24">
        <div className="card">
          <h3 className="font-bold mb-16">📊 Hours by Employee</h3>
          {hoursData.length === 0 ? (
            <div className="empty-state"><span className="empty-state-icon">📊</span><span className="empty-state-text">No data yet</span></div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={hoursData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(148,163,184,0.1)" />
                <XAxis dataKey="name" tick={{ fill: '#94a3b8', fontSize: 12 }} axisLine={false} />
                <YAxis tick={{ fill: '#94a3b8', fontSize: 12 }} axisLine={false} />
                <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid rgba(148,163,184,0.2)', borderRadius: 8, color: '#f1f5f9' }} />
                <Bar dataKey="hours" fill="url(#blueGrad)" radius={[4, 4, 0, 0]} />
                <defs>
                  <linearGradient id="blueGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#3b82f6" />
                    <stop offset="100%" stopColor="#8b5cf6" />
                  </linearGradient>
                </defs>
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        <div className="card">
          <h3 className="font-bold mb-16">🎯 Task Status Breakdown</h3>
          {statusData.every(s => s.value === 0) ? (
            <div className="empty-state"><span className="empty-state-icon">🎯</span><span className="empty-state-text">No data yet</span></div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie data={statusData} cx="50%" cy="50%" innerRadius={55} outerRadius={85} paddingAngle={4} dataKey="value">
                  {statusData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid rgba(148,163,184,0.2)', borderRadius: 8, color: '#f1f5f9' }} />
                <Legend wrapperStyle={{ color: '#94a3b8', fontSize: 12 }} />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      <div className="card">
        <h3 className="font-bold mb-16">🕒 Recent Work Logs</h3>
        {recentLogs.length === 0 ? (
          <div className="empty-state"><span className="empty-state-icon">📋</span><span className="empty-state-text">No work logs yet</span></div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr><th>Employee</th><th>Task</th><th>Hours</th><th>Date</th><th>Status</th></tr>
              </thead>
              <tbody>
                {recentLogs.map(log => (
                  <tr key={log.id}>
                    <td className="td-name">{log.employeeName}</td>
                    <td>{log.taskName}</td>
                    <td className="text-blue font-bold">{log.hoursWorked}h</td>
                    <td className="td-muted">{log.workDate}</td>
                    <td>{statusBadge(log.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
