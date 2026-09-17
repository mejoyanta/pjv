import React, { useState, useEffect } from 'react';
import Subheader from '../../layouts/Subheader';
import Alert from '../../components/common/Alert';
import { barcodeService } from '../../services/basicConfigService';
import { productService } from '../../services/basicConfigService';

export default function BarcodeGeneratorPage() {
  const [products, setProducts] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState('');
  const [barcodeCode, setBarcodeCode] = useState('');
  const [productName, setProductName] = useState('');
  const [productPrice, setProductPrice] = useState('100.00');
  const [quantity, setQuantity] = useState(6);
  const [itemsToPrint, setItemsToPrint] = useState([]);
  const [isPdfLoading, setIsPdfLoading] = useState(false);
  const [alert, setAlert] = useState(null);

  useEffect(() => {
    productService.load({ start: 0, length: 100 })
      .then(res => {
        if (res && res.data) {
          setProducts(res.data);
          if (res.data.length > 0) {
            handleSelectProduct(res.data[0]);
          }
        }
      })
      .catch(console.error);
  }, []);

  const handleSelectProduct = (prod) => {
    setSelectedProduct(prod.id);
    setBarcodeCode(prod.hsCode || `PROD-${prod.id}`);
    setProductName(prod.name);
  };

  const addItemToQueue = () => {
    if (!barcodeCode || !productName) {
      setAlert({ type: 'warning', message: 'Barcode code and product name are required' });
      return;
    }
    const newItem = {
      code: barcodeCode,
      name: productName,
      price: productPrice,
      quantity: parseInt(quantity, 10) || 1
    };
    setItemsToPrint([...itemsToPrint, newItem]);
    setAlert({ type: 'success', message: `Added ${newItem.quantity} barcode labels to print queue` });
  };

  const removeItem = (idx) => {
    setItemsToPrint(itemsToPrint.filter((_, i) => i !== idx));
  };

  const handleDownloadPdf = async () => {
    const queue = itemsToPrint.length > 0 ? itemsToPrint : [{
      code: barcodeCode || 'PROD-001',
      name: productName || 'Sample Product',
      price: productPrice,
      quantity: parseInt(quantity, 10) || 6
    }];

    setIsPdfLoading(true);
    try {
      await barcodeService.downloadBarcodePdf(queue);
      setAlert({ type: 'success', message: 'Barcode sheet PDF downloaded successfully!' });
    } catch (err) {
      setAlert({ type: 'danger', message: 'Failed to generate barcode PDF sheet' });
    } finally {
      setIsPdfLoading(false);
    }
  };

  return (
    <div>
      <Subheader
        title="Barcode Generator"
        breadcrumbs={[
          { label: 'Basic Configuration' },
          { label: 'Barcode' }
        ]}
        actions={
          <button className="btn btn-brand" onClick={handleDownloadPdf} disabled={isPdfLoading}>
            <i className="bi bi-printer"></i>
            <span>{isPdfLoading ? 'Generating...' : 'Print Barcode Sheet (PDF)'}</span>
          </button>
        }
      />

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      <div className="row" style={{ display: 'flex', gap: 20 }}>
        {/* Left Column: Form Controls */}
        <div style={{ flex: 1 }}>
          <div className="kt-portlet" style={{ background: '#fff', borderRadius: 4, padding: 20, boxShadow: '0 0 13px 0 rgba(82,63,105,.05)' }}>
            <h5 style={{ fontWeight: 600, marginBottom: 15, color: '#48465b' }}>Barcode Configuration</h5>

            <div className="form-group" style={{ marginBottom: 15 }}>
              <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Select Existing Product</label>
              <select
                className="form-control"
                value={selectedProduct}
                onChange={(e) => {
                  const prod = products.find(p => p.id.toString() === e.target.value);
                  if (prod) handleSelectProduct(prod);
                }}
              >
                <option value="">-- Choose Product --</option>
                {products.map(p => (
                  <option key={p.id} value={p.id}>{p.name} ({p.hsCode || 'No HS'})</option>
                ))}
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 15 }}>
              <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Barcode Text / Code *</label>
              <input
                type="text"
                className="form-control"
                required
                placeholder="e.g. 5407.42.00 or 123456789"
                value={barcodeCode}
                onChange={(e) => setBarcodeCode(e.target.value)}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 15 }}>
              <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Product Label Title *</label>
              <input
                type="text"
                className="form-control"
                required
                placeholder="e.g. Artificial fibre yarn"
                value={productName}
                onChange={(e) => setProductName(e.target.value)}
              />
            </div>

            <div className="row" style={{ display: 'flex', gap: 15, marginBottom: 15 }}>
              <div style={{ flex: 1 }}>
                <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Price (Optional)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 150.00"
                  value={productPrice}
                  onChange={(e) => setProductPrice(e.target.value)}
                />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ fontWeight: 600, marginBottom: 5, display: 'block' }}>Label Quantity</label>
                <input
                  type="number"
                  min="1"
                  max="100"
                  className="form-control"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                />
              </div>
            </div>

            <div style={{ display: 'flex', gap: 10 }}>
              <button className="btn btn-brand" onClick={addItemToQueue}>
                <i className="bi bi-plus-circle"></i> Add to Print Queue
              </button>
              <button className="btn btn-outline-danger" onClick={handleDownloadPdf} disabled={isPdfLoading}>
                <i className="bi bi-file-earmark-pdf"></i> Quick Print Now
              </button>
            </div>
          </div>
        </div>

        {/* Right Column: Preview & Print Queue */}
        <div style={{ flex: 1 }}>
          <div className="kt-portlet" style={{ background: '#fff', borderRadius: 4, padding: 20, boxShadow: '0 0 13px 0 rgba(82,63,105,.05)' }}>
            <h5 style={{ fontWeight: 600, marginBottom: 15, color: '#48465b' }}>Barcode Sticker Preview</h5>

            {/* Sticker Mockup */}
            <div style={{
              border: '2px dashed #366cfb',
              borderRadius: 6,
              padding: 20,
              textAlign: 'center',
              background: '#f7f8fa',
              marginBottom: 20
            }}>
              <div style={{ fontWeight: 600, fontSize: 13, marginBottom: 5, color: '#333' }}>
                {productName || 'Sample Product Label'}
              </div>
              <div style={{
                background: '#fff',
                padding: '10px 20px',
                display: 'inline-block',
                borderRadius: 4,
                border: '1px solid #ddd',
                margin: '10px 0'
              }}>
                <div style={{ letterSpacing: 4, fontFamily: 'monospace', fontWeight: 700, fontSize: 22 }}>
                  ||| | |||| | || ||| | |||
                </div>
                <div style={{ fontSize: 12, letterSpacing: 2, marginTop: 4, color: '#555' }}>
                  {barcodeCode || 'PROD-12345'}
                </div>
              </div>
              {productPrice && (
                <div style={{ fontSize: 12, fontWeight: 600, color: '#0abb87' }}>
                  Price: ৳ {productPrice}
                </div>
              )}
            </div>

            {/* Print Queue Table */}
            <h6 style={{ fontWeight: 600, marginBottom: 10 }}>Print Queue ({itemsToPrint.length} items)</h6>
            {itemsToPrint.length === 0 ? (
              <p style={{ color: '#999', fontSize: 13 }}>Queue is empty. Use "Quick Print Now" or click "Add to Print Queue".</p>
            ) : (
              <table className="table table-sm table-bordered">
                <thead>
                  <tr style={{ background: '#f4f5f8' }}>
                    <th>Code</th>
                    <th>Product</th>
                    <th>Qty</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {itemsToPrint.map((it, idx) => (
                    <tr key={idx}>
                      <td><code>{it.code}</code></td>
                      <td>{it.name}</td>
                      <td>{it.quantity}</td>
                      <td>
                        <button className="btn btn-xs btn-label-danger" onClick={() => removeItem(idx)}>
                          <i className="bi bi-x"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
