import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { companyService } from '../../services/companyService';

export default function CompanyDocumentsPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState(null);

  // Upload state
  const [title, setTitle] = useState('');
  const [expiryDate, setExpiryDate] = useState('');
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    loadDocs();
  }, [slug]);

  const loadDocs = async () => {
    setLoading(true);
    try {
      const res = await companyService.getDocuments(slug);
      if (res && res.data) {
        setDocuments(res.data);
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setAlert({ type: 'danger', message: 'Please choose a file to upload.' });
      return;
    }

    setUploading(true);
    const formData = new FormData();
    formData.append('title', title);
    if (expiryDate) formData.append('expiryDate', expiryDate);
    formData.append('file', file);

    try {
      const res = await companyService.uploadDocument(slug, formData);
      if (res && res.data) {
        setDocuments(res.data);
        setAlert({ type: 'success', message: 'Document uploaded successfully!' });
        setTitle('');
        setExpiryDate('');
        setFile(null);
        e.target.reset();
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (docId) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;
    try {
      const res = await companyService.deleteDocument(slug, docId);
      if (res && res.data) {
        setDocuments(res.data);
        setAlert({ type: 'success', message: 'Document deleted successfully!' });
      }
    } catch (err) {
      setAlert({ type: 'danger', message: err.message });
    }
  };

  return (
    <div>
      <Subheader
        title={`Company Documents: ${slug}`}
        breadcrumbs={[
          { label: 'Company', link: '/company' },
          { label: 'Documents' }
        ]}
        actions={
          <button className="btn btn-secondary" onClick={() => navigate('/company')}>
            <i className="bi bi-arrow-left"></i> Back to Companies
          </button>
        }
      />

      {alert && <Alert type={alert.type} message={alert.message} onClose={() => setAlert(null)} />}

      <div className="row">
        {/* Upload Card */}
        <div className="col-md-4">
          <div className="kt-portlet">
            <div className="kt-portlet__head">
              <div className="kt-portlet__head-title">
                <i className="bi bi-cloud-arrow-up" style={{ color: 'var(--kt-brand)' }}></i>
                <span>Upload New Document</span>
              </div>
            </div>
            <div className="kt-portlet__body">
              <form onSubmit={handleUpload}>
                <div className="form-group">
                  <label className="form-label">Document Title *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Trade License, Tax Return..."
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Expiry Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={expiryDate}
                    onChange={(e) => setExpiryDate(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">File Attachment *</label>
                  <input
                    type="file"
                    className="form-control"
                    required
                    onChange={(e) => setFile(e.target.files[0])}
                  />
                </div>

                <button type="submit" className="btn btn-brand" style={{ width: '100%' }} disabled={uploading}>
                  {uploading ? 'Uploading...' : 'Upload Document'}
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* Documents List */}
        <div className="col-md-8">
          <div className="kt-portlet">
            <div className="kt-portlet__head">
              <div className="kt-portlet__head-title">
                <i className="bi bi-folder2-open" style={{ color: 'var(--kt-brand)' }}></i>
                <span>Attached Documents ({documents.length})</span>
              </div>
            </div>
            <div className="kt-portlet__body">
              {loading ? (
                <div style={{ textAlign: 'center', padding: 20 }}>Loading documents...</div>
              ) : documents.length === 0 ? (
                <div style={{ textAlign: 'center', padding: 30, color: '#959cb6' }}>
                  No documents uploaded for this company yet.
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table">
                    <thead>
                      <tr>
                        <th>Title</th>
                        <th>Filename</th>
                        <th>Expiry Date</th>
                        <th>Uploaded On</th>
                        <th style={{ textAlign: 'center' }}>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {documents.map((doc) => (
                        <tr key={doc.id}>
                          <td style={{ fontWeight: 600, color: '#48465b' }}>{doc.title}</td>
                          <td style={{ fontSize: 12 }}>{doc.fileName}</td>
                          <td>{doc.expiryDate || '—'}</td>
                          <td style={{ fontSize: 12, color: '#959cb6' }}>{doc.createdAt}</td>
                          <td style={{ textAlign: 'center' }}>
                            <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
                              <a
                                href={`http://localhost:8080${doc.fileUrl}`}
                                target="_blank"
                                rel="noreferrer"
                                className="btn btn-sm btn-label-brand btn-circle btn-icon"
                                title="View Document"
                              >
                                <i className="bi bi-eye"></i>
                              </a>
                              <button
                                type="button"
                                className="btn btn-sm btn-label-danger btn-circle btn-icon"
                                title="Delete Document"
                                onClick={() => handleDelete(doc.id)}
                              >
                                <i className="bi bi-trash"></i>
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
