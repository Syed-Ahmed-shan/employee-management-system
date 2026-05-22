import { useEffect, useState } from 'react';
import { getEmployees, createEmployee, updateEmployee, deleteEmployee } from '../api/api';
import { useAuth } from '../context/AuthContext';

const EMPTY = {
  firstName: '', lastName: '', email: '', phone: '',
  department: '', jobTitle: '', salary: '', joiningDate: '',
  empCode: '', role: 'ROLE_EMPLOYEE'
};

export default function Employees() {
  const [employees, setEmployees] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editData, setEditData] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [deleteId, setDeleteId] = useState(null);
  const { user } = useAuth();
  const isAdmin = user?.role === 'ROLE_ADMIN';

  const load = async () => {
    try {
      const res = await getEmployees();
      setEmployees(res.data.data || []);
      setFiltered(res.data.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    const q = search.toLowerCase();
    setFiltered(employees.filter(e =>
      `${e.firstName} ${e.lastName} ${e.email} ${e.department} ${e.jobTitle}`
        .toLowerCase().includes(q)
    ));
  }, [search, employees]);

  const openAdd = () => { setEditData(null); setForm(EMPTY); setError(''); setShowModal(true); };
  const openEdit = (emp) => {
    setEditData(emp);
    setForm({
      firstName: emp.firstName, lastName: emp.lastName,
      email: emp.email, phone: emp.phone || '',
      department: emp.department || '', jobTitle: emp.jobTitle || '',
      salary: emp.salary || '', joiningDate: emp.joiningDate || '',
      empCode: emp.empCode || '', role: emp.role || 'ROLE_EMPLOYEE'
    });
    setError('');
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault(); setError(''); setSaving(true);
    try {
      if (editData) {
        await updateEmployee(editData.id, { ...form, salary: Number(form.salary) });
      } else {
        await createEmployee({ ...form, salary: Number(form.salary) });
      }
      setShowModal(false);
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Something went wrong');
    } finally { setSaving(false); }
  };

  const handleDelete = async (id) => {
    try { await deleteEmployee(id); setDeleteId(null); load(); }
    catch (err) { alert(err.response?.data?.message || 'Delete failed'); }
  };

  const statusBadge = (s) => (
    <span className={`badge ${s === 'ACTIVE' ? 'badge-active' : 'badge-inactive'}`}>{s}</span>
  );

  if (loading) return <div className="loading-state"><div className="spinner" /><span>Loading employees...</span></div>;

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">👥 <span>Employees</span></h1>
          <p className="page-subtitle">{employees.length} employees in the system</p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={openAdd}>➕ Add Employee</button>
        )}
      </div>

      <div className="filter-bar">
        <div className="search-wrapper">
          <span className="search-icon">🔍</span>
          <input className="search-input" placeholder="Search by name, email, department..."
            value={search} onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      <div className="card" style={{ padding: 0 }}>
        {filtered.length === 0 ? (
          <div className="empty-state">
            <span className="empty-state-icon">👥</span>
            <span className="empty-state-text">No employees found</span>
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="table-header">
              <thead>
                <tr>
                  <th className="table-cell">Emp ID</th>
                  <th className="table-cell">Name</th>
                  <th className="table-cell">Department</th>
                  <th className="table-cell">Role / Title</th>
                  <th className="table-cell">Email</th>
                  <th>Status</th>
                  {isAdmin && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {filtered.map((emp, i) => (
                  <tr key={emp.id}>
                    <td className="table-cell">
                      <span className="badge badge-employee">{emp.empCode}</span>
                    </td>
                    <td className="table-cell" style={{ fontWeight: '500' }}>{emp.firstName} {emp.lastName}</td>
                    <td className="table-cell">{emp.department}</td>
                    <td className="table-cell">{emp.jobTitle}</td>
                    <td className="table-cell" style={{ color: 'var(--text-secondary)' }}>{emp.email}</td>
                    <td>{statusBadge(emp.status)}</td>
                    {isAdmin && (
                      <td>
                        <div className="flex gap-8">
                          <button className="btn btn-secondary btn-sm" onClick={() => openEdit(emp)}>✏️ Edit</button>
                          <button className="btn btn-danger btn-sm" onClick={() => setDeleteId(emp.id)}>🗑️</button>
                        </div>
                      </td>
                    )}
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
          <div className="modal modal-lg" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{editData ? '✏️ Edit Employee' : '➕ Add New Employee'}</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>

            {error && <div className="alert alert-error">⚠️ {error}</div>}

            <form onSubmit={handleSave}>
              <div className="form-grid mb-16">
                <div className="form-group">
                  <label className="form-label">First Name *</label>
                  <input className="form-input" value={form.firstName} required
                    onChange={e => setForm({ ...form, firstName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Last Name *</label>
                  <input className="form-input" value={form.lastName} required
                    onChange={e => setForm({ ...form, lastName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone *</label>
                  <input className="form-input" value={form.phone} required
                    onChange={e => setForm({ ...form, phone: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Email *</label>
                  <input className="form-input" type="email" value={form.email} required
                    onChange={e => setForm({ ...form, email: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Department *</label>
                  <input className="form-input" value={form.department} required
                    onChange={e => setForm({ ...form, department: e.target.value })} />
                </div>
                <div className="form-group mb-16">
                  <label className="form-label">System Role</label>
                  <select 
                    className="form-input" 
                    value={form.role} 
                    onChange={e => setForm({...form, role: e.target.value})}
                  >
                    <option value="ROLE_EMPLOYEE">Employee</option>
                    <option value="ROLE_MANAGER">Manager</option>
                  </select>
                </div>
                <div className="form-group mb-16">
                  <label className="form-label">Job Title</label>
                  <input className="form-input" value={form.jobTitle} required
                    onChange={e => setForm({ ...form, jobTitle: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Salary (₹) *</label>
                  <input className="form-input" type="number" value={form.salary} required
                    onChange={e => setForm({ ...form, salary: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Joining Date *</label>
                  <input className="form-input" type="date" value={form.joiningDate} required
                    onChange={e => setForm({ ...form, joiningDate: e.target.value })} />
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

      {/* Delete Confirm Modal */}
      {deleteId && (
        <div className="modal-overlay" onClick={() => setDeleteId(null)}>
          <div className="modal" style={{ maxWidth: 420 }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">🗑️ Confirm Delete</h2>
              <button className="modal-close" onClick={() => setDeleteId(null)}>×</button>
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              Are you sure you want to delete this employee? This action cannot be undone.
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
