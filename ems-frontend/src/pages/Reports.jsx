import { useEffect, useState } from 'react';
import { getAllReports, exportAllCSV, exportEmployeeCSV } from '../api/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Cell
} from 'recharts';

const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#06b6d4'];

export default function Reports() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getAllReports()
      .then(res => setReports(res.data.data || []))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const downloadCSV = async (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename; a.click();
    window.URL.revokeObjectURL(url);
  };

  const handleExportAll = async () => {
    try {
      const res = await exportAllCSV();
      downloadCSV(res.data, 'all-worklogs.csv');
    } catch (e) { alert('Export failed'); }
  };

  const handleExportEmployee = async (id, name) => {
    try {
      const res = await exportEmployeeCSV(id);
      downloadCSV(res.data, `${name.replace(' ', '-')}-worklogs.csv`);
    } catch (e) { alert('Export failed'); }
  };

  const filtered = reports.filter(r =>
    `${r.employeeName} ${r.department}`.toLowerCase().includes(search.toLowerCase())
  );

  const chartData = reports.map(r => ({
    name: r.employeeName.split(' ')[0],
    hours: r.totalHoursWorked,
    tasks: r.totalTasks,
  }));

  const statusColor = { COMPLETED: '#10b981', IN_PROGRESS: '#f59e0b', PENDING: '#64748b' };

  if (loading) return <div className="loading-state"><div className="spinner" /><span>Loading reports...</span></div>;

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">📈 <span>Reports</span></h1>
          <p className="page-subtitle">Performance overview for {reports.length} employees</p>
        </div>
        <button className="btn btn-success" onClick={handleExportAll}>
          ⬇️ Export All CSV
        </button>
      </div>

      {/* Chart */}
      {chartData.length > 0 && (
        <div className="card mb-24">
          <h3 className="font-bold mb-16">📊 Hours Worked by Employee</h3>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={chartData} barSize={36}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(148,163,184,0.1)" />
              <XAxis dataKey="name" tick={{ fill: '#94a3b8', fontSize: 12 }} axisLine={false} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 12 }} axisLine={false} />
              <Tooltip
                contentStyle={{ background: '#1e293b', border: '1px solid rgba(148,163,184,0.2)', borderRadius: 8, color: '#f1f5f9' }}
                formatter={(v, n) => [n === 'hours' ? `${v}h` : v, n === 'hours' ? 'Hours' : 'Tasks']}
              />
              {chartData.map((_, i) => (
                <Cell key={i} fill={COLORS[i % COLORS.length]} />
              ))}
              <Bar dataKey="hours" radius={[6, 6, 0, 0]}>
                {chartData.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Search */}
      <div className="filter-bar mb-16">
        <div className="search-wrapper">
          <span className="search-icon">🔍</span>
          <input className="search-input" placeholder="Search by name or department..."
            value={search} onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      {/* Report Cards Grid */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <span className="empty-state-icon">📈</span>
          <span className="empty-state-text">No report data available</span>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 18 }}>
          {filtered.map((r, i) => (
            <div key={r.employeeId} className="report-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div className="report-name">{r.employeeName}</div>
                  <div className="report-dept">
                    <span style={{ color: COLORS[i % COLORS.length] }}>●</span>{' '}
                    {r.department} · {r.jobTitle}
                  </div>
                </div>
                <button className="btn btn-secondary btn-sm"
                  onClick={() => handleExportEmployee(r.employeeId, r.employeeName)}
                  title="Export CSV">
                  ⬇️
                </button>
              </div>

              <div className="report-stats">
                <div className="report-stat-item">
                  <div className="report-stat-val">{r.totalTasks}</div>
                  <div className="report-stat-label">Tasks</div>
                </div>
                <div className="report-stat-item">
                  <div className="report-stat-val">{r.totalHoursWorked}h</div>
                  <div className="report-stat-label">Hours</div>
                </div>
                <div className="report-stat-item">
                  <div className="report-stat-val">{r.averageHoursPerTask}h</div>
                  <div className="report-stat-label">Avg/Task</div>
                </div>
              </div>

              <div className="report-status-bar mt-16">
                {[
                  { label: 'Completed', val: r.completedTasks, color: '#10b981' },
                  { label: 'In Progress', val: r.inProgressTasks, color: '#f59e0b' },
                  { label: 'Pending', val: r.pendingTasks, color: '#64748b' },
                ].map(s => (
                  <div key={s.label} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.76rem', color: 'var(--text-secondary)' }}>
                    <div className="status-dot" style={{ background: s.color }} />
                    {s.val} {s.label}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
