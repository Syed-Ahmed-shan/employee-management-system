import { useEffect, useState } from 'react';
import {
  getWorkLogs, getEmployees, createWorkLog, updateWorkLog, deleteWorkLog
} from '../api/api';
import { useAuth } from '../context/AuthContext';

const STATUSES = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];

const EMPTY = {
  employeeId: '', taskName: '', description: '',
  hoursWorked: '0', workDate: new Date().toISOString().split('T')[0], status: 'PENDING',
  priority: 'MEDIUM', deadline: ''
};

export default function WorkLogs() {
  const [logs, setLogs] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterEmp, setFilterEmp] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editData, setEditData] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [deleteId, setDeleteId] = useState(null);
  const { user } = useAuth();

  const load = async () => {
    try {
      const [wlRes, empRes] = await Promise.all([getWorkLogs(), getEmployees()]);
      setLogs(wlRes.data.data || []);
      setFiltered(wlRes.data.data || []);
      setEmployees(empRes.data.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    let data = [...logs];
    if (filterEmp) data = data.filter(l => l.employeeId === Number(filterEmp));
    if (filterStatus) data = data.filter(l => l.status === filterStatus);
    if (search) data = data.filter(l =>
      `${l.employeeName} ${l.taskName}`.toLowerCase().includes(search.toLowerCase())
    );
    setFiltered(data);
  }, [logs, filterEmp, filterStatus, search]);

  const openAdd = () => { setEditData(null); setForm(EMPTY); setError(''); setShowModal(true); };
  const openEdit = (log) => {
    setEditData(log);
    setForm({
      employeeId: log.employeeId, taskName: log.taskName,
      description: log.description || '', hoursWorked: log.hoursWorked,
      workDate: log.workDate, status: log.status,
      priority: log.priority || 'MEDIUM', deadline: log.deadline || ''
    });
    setError(''); setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault(); setError(''); setSaving(true);
    try {
      const payload = { 
        ...form, 
        employeeId: Number(form.employeeId), 
        hoursWorked: Number(form.hoursWorked),
        assignedById: user?.employeeId,
        assignedByName: user?.username,
        assignedByRole: user?.role
      };
      if (editData) await updateWorkLog(editData.id, payload);
      else await createWorkLog(payload);
      setShowModal(false); load();
    } catch (err) {
      setError(err.response?.data?.message || 'Something went wrong');
    } finally { setSaving(false); }
  };

  const handleDelete = async (id) => {
    try { await deleteWorkLog(id); setDeleteId(null); load(); }
    catch (err) { alert(err.response?.data?.message || 'Delete failed'); }
  };

  const statusBadge = (s) => {
    const map = { COMPLETED: 'badge-completed', IN_PROGRESS: 'badge-progress', PENDING: 'badge-pending' };
    const label = { COMPLETED: 'Completed', IN_PROGRESS: 'In Progress', PENDING: 'Pending' };
    return <span className={`badge ${map[s] || ''}`}>{label[s] || s}</span>;
  };

  if (loading) return <div className="loading-state"><div className="spinner" /><span>Loading work logs...</span></div>;

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">📋 <span>Work Logs</span></h1>
          <p className="page-subtitle">{logs.length} total work log entries</p>
        </div>
        <button className="btn btn-primary" onClick={openAdd}>➕ Add Work Log</button>
      </div>

      {/* Filters */}
      <div className="filter-bar">
        <div className="search-wrapper" style={{ minWidth: 240 }}>
          <span className="search-icon">🔍</span>
          <input className="search-input" placeholder="Search task or employee..."
            value={search} onChange={e => setSearch(e.target.value)} />
        </div>

        <select className="form-select" style={{ width: 180 }}
          value={filterEmp} onChange={e => setFilterEmp(e.target.value)}>
          <option value="">All Employees</option>
          {employees.map(emp => (
            <option key={emp.id} value={emp.id}>
              [{emp.empCode}] {emp.firstName} {emp.lastName}
            </option>
          ))}
        </select>

        <select className="form-select" style={{ width: 160 }}
          value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
          <option value="">All Statuses</option>
          {STATUSES.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
        </select>

        {(filterEmp || filterStatus || search) && (
          <button className="btn btn-secondary btn-sm"
            onClick={() => { setFilterEmp(''); setFilterStatus(''); setSearch(''); }}>
            ✕ Clear
          </button>
        )}
      </div>

      <div className="card" style={{ padding: 0 }}>
        {filtered.length === 0 ? (
          <div className="empty-state">
            <span className="empty-state-icon">📋</span>
            <span className="empty-state-text">No work logs found</span>
          </div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Employee</th><th>Task</th>
                  <th>Hours</th><th>Date</th><th>Status</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((log, i) => (
                  <tr key={log.id}>
                    <td className="td-muted">{i + 1}</td>
                    <td className="td-name">{log.employeeName}</td>
                    <td>
                      <div className="font-bold" style={{ fontSize: '0.86rem' }}>{log.taskName}</div>
                      {log.description && (
                        <div className="td-muted" style={{ fontSize: '0.76rem', marginTop: 2 }}>
                          {log.description.length > 50 ? log.description.slice(0, 50) + '…' : log.description}
                        </div>
                      )}
                      <div className="td-muted" style={{ fontSize: '0.7rem', marginTop: 2 }}>
                        Priority: {log.priority} | Due: {log.deadline || 'N/A'}
                      </div>
                    </td>
                    <td className="text-blue font-bold">{log.hoursWorked}h</td>
                    <td className="td-muted">{log.workDate}</td>
                    <td>{statusBadge(log.status)}</td>
                    <td>
                      <div className="flex gap-8">
                        <button className="btn btn-secondary btn-sm" onClick={() => openEdit(log)}>✏️</button>
                        <button className="btn btn-danger btn-sm" onClick={() => setDeleteId(log.id)}>🗑️</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add/Edit Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editData ? '✏️ Edit Work Log' : '➕ New Work Log'}</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>

            {error && <div className="alert alert-error">⚠️ {error}</div>}

            <form onSubmit={handleSave}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                <div className="form-group">
                  <label className="form-label">Employee *</label>
                  <select className="form-select" value={form.employeeId} required
                    onChange={e => setForm({ ...form, employeeId: e.target.value })}>
                    <option value="">Select employee...</option>
                    {employees.map(emp => (
                      <option key={emp.id} value={emp.id}>
                        [{emp.empCode}] {emp.firstName} {emp.lastName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Task Name *</label>
                  <input className="form-input" value={form.taskName} required
                    onChange={e => setForm({ ...form, taskName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Description</label>
                  <input className="form-input" value={form.description}
                    onChange={e => setForm({ ...form, description: e.target.value })} />
                </div>
                <div className="form-grid">
                  <div className="form-group">
                    <label className="form-label">Hours Worked *</label>
                    <input className="form-input" type="number" step="0.5" min="0.5" value={form.hoursWorked} required
                      onChange={e => setForm({ ...form, hoursWorked: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Work Date *</label>
                    <input className="form-input" type="date" value={form.workDate} required
                      onChange={e => setForm({ ...form, workDate: e.target.value })} />
                  </div>
                </div>
                <div className="form-grid">
                  <div className="form-group">
                    <label className="form-label">Priority</label>
                    <select className="form-select" value={form.priority}
                      onChange={e => setForm({ ...form, priority: e.target.value })}>
                      <option value="LOW">Low</option>
                      <option value="MEDIUM">Medium</option>
                      <option value="HIGH">High</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Deadline</label>
                    <input className="form-input" type="date" value={form.deadline}
                      onChange={e => setForm({ ...form, deadline: e.target.value })} />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select className="form-select" value={form.status}
                    onChange={e => setForm({ ...form, status: e.target.value })}>
                    {STATUSES.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
                  </select>
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? '⏳ Saving...' : (editData ? '✅ Update' : '➕ Create')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirm */}
      {deleteId && (
        <div className="modal-overlay" onClick={() => setDeleteId(null)}>
          <div className="modal" style={{ maxWidth: 400 }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">🗑️ Delete Work Log?</h2>
              <button className="modal-close" onClick={() => setDeleteId(null)}>×</button>
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              This work log entry will be permanently deleted.
            </p>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setDeleteId(null)}>Cancel</button>
              <button className="btn btn-danger" onClick={() => handleDelete(deleteId)}>🗑️ Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
