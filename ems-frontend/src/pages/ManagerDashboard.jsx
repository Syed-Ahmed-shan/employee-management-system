import { useEffect, useState } from 'react';
import { getEmployees, getWorkLogs, createWorkLog } from '../api/api';

export default function ManagerDashboard() {
  const [employees, setEmployees] = useState([]);
  const [recentLogs, setRecentLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Assign Task Form
  const [taskForm, setTaskForm] = useState({ employeeId: '', taskName: '', workDate: '', hoursWorked: 0 });
  const [assigning, setAssigning] = useState(false);
  const [msg, setMsg] = useState('');

  const loadData = async () => {
    try {
      const [empRes, wlRes] = await Promise.all([getEmployees(), getWorkLogs()]);
      setEmployees(empRes.data.data || []);
      setRecentLogs((wlRes.data.data || []).slice(0, 8)); // latest 8
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleAssign = async (e) => {
    e.preventDefault();
    setAssigning(true); setMsg('');
    try {
      // Find logged-in manager info from context, wait we don't have user ID in token easily,
      // but the backend will use SecurityContext or we pass null.
      // Actually we just pass what the API needs
      await createWorkLog({ 
        employeeId: Number(taskForm.employeeId),
        taskName: taskForm.taskName,
        workDate: taskForm.workDate,
        hoursWorked: 0,
        status: 'PENDING'
      });
      setMsg('Task successfully assigned!');
      setTaskForm({ employeeId: '', taskName: '', workDate: '', hoursWorked: 0 });
      loadData();
    } catch (err) {
      setMsg('Failed to assign task.');
    } finally {
      setAssigning(false);
    }
  };

  const statusBadge = (s) => (
    <span className={`badge ${s === 'COMPLETED' ? 'badge-completed' : s === 'IN_PROGRESS' ? 'badge-progress' : 'badge-pending'}`}>
      {s.replace('_', ' ')}
    </span>
  );

  if (loading) return <div className="loading-state"><div className="spinner" /><span>Loading Manager View...</span></div>;

  return (
    <div className="fade-in">
      <div className="page-header">
        <div>
          <h1 className="page-title">Manager <span>Dashboard</span></h1>
          <p className="page-subtitle">Assign tasks and monitor your team's progress.</p>
        </div>
      </div>

      <div className="grid-2 mb-24">
        {/* Assign Task Card */}
        <div className="card">
          <h3 className="font-bold mb-16">🎯 Quick Assign Task</h3>
          {msg && <div className={`alert ${msg.includes('success') ? 'alert-success' : 'alert-error'}`}>{msg}</div>}
          
          <form onSubmit={handleAssign} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div className="form-group">
              <label className="form-label">Employee</label>
              <select className="form-select" value={taskForm.employeeId} required onChange={e => setTaskForm({...taskForm, employeeId: e.target.value})}>
                <option value="">Select Employee...</option>
                {employees.map(e => <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Task Description</label>
              <input className="form-input" required value={taskForm.taskName} onChange={e => setTaskForm({...taskForm, taskName: e.target.value})} placeholder="What needs to be done?" />
            </div>
            <div className="form-group">
              <label className="form-label">Target Date</label>
              <input className="form-input" type="date" required value={taskForm.workDate} onChange={e => setTaskForm({...taskForm, workDate: e.target.value})} />
            </div>
            <button className="btn btn-primary mt-8" type="submit" disabled={assigning}>
              {assigning ? 'Assigning...' : '➕ Assign Task'}
            </button>
          </form>
        </div>

        {/* Team Overview Card */}
        <div className="card">
          <h3 className="font-bold mb-16">📋 Recent Team Activity</h3>
          <div className="table-wrapper">
            <table>
              <thead><tr><th>Employee</th><th>Task</th><th>Status</th></tr></thead>
              <tbody>
                {recentLogs.map(log => (
                  <tr key={log.id}>
                    <td className="td-name">{log.employeeName}</td>
                    <td>{log.taskName}</td>
                    <td>{statusBadge(log.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
