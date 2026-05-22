import React from 'react';
import './DemoGuideModal.css';

export default function DemoGuideModal({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose} style={{ zIndex: 9999 }}>
      <div className="modal modal-lg guide-modal" onClick={(e) => e.stopPropagation()}>
        
        {/* Header Section */}
        <div className="guide-header">
          <div>
            <h2 className="guide-title">🚀 Project Instructions & Demo Access</h2>
            <p className="guide-subtitle">Please read the instructions carefully before testing the application.</p>
          </div>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="guide-content">
          
          {/* Demo Login Credentials */}
          <div className="guide-section">
            <h3 className="section-heading">🔑 Demo Login Credentials</h3>
            <div className="credentials-grid">
              
              <div className="credential-card admin-card">
                <div className="card-header">👑 Admin Login</div>
                <div className="card-body">
                  <p><strong>Username:</strong> Ahmed</p>
                  <p><strong>Password:</strong> admin</p>
                </div>
              </div>

              <div className="credential-card manager-card">
                <div className="card-header">👥 Manager Login</div>
                <div className="card-body">
                  <p><strong>Manager ID:</strong> MAN-0387B</p>
                  <p><strong>Name:</strong> Syed Nawaz</p>
                  <p><strong>Password:</strong> qwerty</p>
                </div>
              </div>

              <div className="credential-card employee-card">
                <div className="card-header">💻 Employee Login</div>
                <div className="card-body">
                  <p><strong>Employee ID:</strong> SAL-A88BD</p>
                  <p><strong>Password:</strong> qwerty</p>
                </div>
              </div>

            </div>
          </div>

          {/* HR Testing Instructions */}
          <div className="guide-section">
            <h3 className="section-heading">📋 HR Testing Instructions</h3>
            
            <div className="flow-steps">
              <div className="flow-step">
                <div className="step-icon admin-icon">👑</div>
                <div className="step-content">
                  <h4>Admin Flow</h4>
                  <ul>
                    <li>Login as <strong>Admin</strong></li>
                    <li>Create Managers and Employees</li>
                    <li>Assign Departments and Job Titles</li>
                    <li>Monitor Overall System Data</li>
                    <li>Track Tasks & View Global Reports</li>
                  </ul>
                </div>
              </div>

              <div className="flow-step">
                <div className="step-icon manager-icon">👥</div>
                <div className="step-content">
                  <h4>Manager Flow</h4>
                  <ul>
                    <li>Login using <strong>Manager ID</strong></li>
                    <li>Navigate to Task Management</li>
                    <li>Create Tasks (Set Priority & Deadline)</li>
                    <li>Assign Tasks to Specific Employees</li>
                    <li>Monitor Employee Progress & Generate Reports</li>
                  </ul>
                </div>
              </div>

              <div className="flow-step">
                <div className="step-icon employee-icon">💻</div>
                <div className="step-content">
                  <h4>Employee Flow</h4>
                  <ul>
                    <li>Login using <strong>Employee ID</strong></li>
                    <li>View Assigned Tasks on Workspace</li>
                    <li>Log Hours Worked</li>
                    <li>Update Task Status (In Progress {'->'} Completed)</li>
                    <li>Submit Work Reports to Managers</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

        </div>
        
        <div className="modal-footer" style={{ borderTop: 'none', marginTop: '10px' }}>
          <button className="btn btn-primary w-full" onClick={onClose} style={{ padding: '14px', fontSize: '1rem' }}>
            I Understand, Let's Test!
          </button>
        </div>

      </div>
    </div>
  );
}
