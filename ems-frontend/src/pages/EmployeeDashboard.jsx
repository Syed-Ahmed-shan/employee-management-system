import { useEffect, useState } from 'react';
import { getWorkLogsByEmployee, updateWorkLog } from '../api/api';
import { useAuth } from '../context/AuthContext';

export default function EmployeeDashboard() {
  const { user } = useAuth();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Update Modal State
  const [showModal, setShowModal] = useState(false);
  const [activeTask, setActiveTask] = useState(null);
  const [form, setForm] = useState({ status: '', hoursWorked: 0 });
  const [updating, setUpdating] = useState(false);

  const loadData = async () => {
    if (!user || !user.employeeId) {
       setLoading(false);
       return;
    }
    try {
      const res = await getWorkLogsByEmployee(user.employeeId);
      setLogs(res.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [user]);

  const openUpdate = (task) => {
    setActiveTask(task);
    setForm({ status: task.status, hoursWorked: task.hoursWorked });
    setShowModal(true);
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setUpdating(true);
    try {
      // Send the update to the backend. The backend WorkLogRequestDTO requires employeeId, taskName, workDate
      await updateWorkLog(activeTask.id, {
        employeeId: activeTask.employeeId,
        taskName: activeTask.taskName,
        workDate: activeTask.workDate,
        status: form.status,
        hoursWorked: Number(form.hoursWorked)
      });
      setShowModal(false);
      loadData();
    } catch (err) {
      alert('Failed to update task');
    } finally {
      setUpdating(false);
    }
  };

  const pendingTasks = logs.filter(l => l.status === 'PENDING');
  const inProgressTasks = logs.filter(l => l.status === 'IN_PROGRESS');
  const completedTasks = logs.filter(l => l.status === 'COMPLETED');

  const TaskCard = ({ task }) => (
    <div style={{ background: 'var(--bg-card)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border)', marginBottom: '12px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
        <h4 style={{ margin: 0, fontSize: '0.95rem' }}>{task.taskName}</h4>
        <span className={`badge ${task.priority === 'HIGH' ? 'badge-inactive' : 'badge-progress'}`} style={{ fontSize: '0.6rem', padding: '2px 6px' }}>
          {task.priority || 'MEDIUM'}
        </span>
      </div>
      
      {task.description && (
        <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginBottom: '12px', marginTop: 0 }}>
          {task.description.length > 60 ? task.description.slice(0, 60) + '...' : task.description}
        </p>
      )}

      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
        <strong>Assigned By:</strong> {task.assignedByName || 'System'} {task.assignedByRole && `(${task.assignedByRole.replace('ROLE_', '')})`}
      </div>
      
      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
        <strong>Deadline:</strong> {task.deadline || 'No deadline'}
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ fontSize: '0.85rem', color: 'var(--accent-blue)', fontWeight: 'bold' }}>{task.hoursWorked}h logged</span>
        <button className="btn btn-secondary btn-sm" onClick={() => openUpdate(task)}>Update</button>
      </div>
    </div>
  );

  if (loading) return <div className="loading-state"><div className="spinner" /><span>Loading My Workspace...</span></div>;

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">My <span>Workspace</span></h1>
          <p className="page-subtitle">Track your tasks and log your hours.</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon orange">🎯</div>
          <div><div className="stat-label">Pending Tasks</div><div className="stat-value">{pendingTasks.length}</div></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon purple">⏳</div>
          <div><div className="stat-label">In Progress</div><div className="stat-value">{inProgressTasks.length}</div></div>
        </div>
        <div className="stat-card">
          <div className="stat-icon green">✅</div>
          <div><div className="stat-label">Completed</div><div className="stat-value">{completedTasks.length}</div></div>
        </div>
      </div>

      <div className="grid-3 mt-24">
        <div>
          <h3 className="font-bold mb-16" style={{ borderBottom: '2px solid var(--accent-orange)', paddingBottom: '8px' }}>To Do (Pending)</h3>
          {pendingTasks.map(t => <TaskCard key={t.id} task={t} />)}
        </div>
        <div>
          <h3 className="font-bold mb-16" style={{ borderBottom: '2px solid var(--accent-purple)', paddingBottom: '8px' }}>In Progress</h3>
          {inProgressTasks.map(t => <TaskCard key={t.id} task={t} />)}
        </div>
        <div>
          <h3 className="font-bold mb-16" style={{ borderBottom: '2px solid var(--accent-green)', paddingBottom: '8px' }}>Done</h3>
          {completedTasks.slice(0, 5).map(t => <TaskCard key={t.id} task={t} />)}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" style={{ maxWidth: '400px' }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Update Task Progress</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleUpdate}>
              <div className="form-group mb-16">
                <label className="form-label">Status</label>
                <select className="form-select" value={form.status} onChange={e => setForm({...form, status: e.target.value})}>
                  <option value="PENDING">Pending</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="COMPLETED">Completed</option>
                </select>
              </div>
              <div className="form-group mb-24">
                <label className="form-label">Total Hours Worked</label>
                <input className="form-input" type="number" step="0.5" min="0" value={form.hoursWorked} onChange={e => setForm({...form, hoursWorked: e.target.value})} />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={updating}>{updating ? 'Saving...' : 'Save Update'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
