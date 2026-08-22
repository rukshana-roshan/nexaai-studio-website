/**
 * TravelMate Administrator CRUD Management Script
 * Implements FR-039 to FR-046 & NFR-010 to NFR-013
 */
const AdminApp = {
  attractions: [],
  selectedAttractionId: null,

  async init() {
    this.bindEvents();
    await this.loadAttractions();
    this.updateStats();
  },

  bindEvents() {
    // Admin Search filter
    const searchInput = document.getElementById('adminTableSearch');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filtered = this.attractions.filter(a =>
          a.name.toLowerCase().includes(query) ||
          a.category.toLowerCase().includes(query) ||
          a.location.toLowerCase().includes(query)
        );
        this.renderTable(filtered);
      });
    }

    // Save Attraction Form submit
    const form = document.getElementById('attractionForm');
    if (form) {
      form.addEventListener('submit', (e) => {
        e.preventDefault();
        this.saveAttraction();
      });
    }
  },

  async loadAttractions() {
    try {
      this.attractions = await ApiService.getAttractions();
      this.renderTable(this.attractions);
      this.updateStats();
    } catch (error) {
      this.showToast('Failed to load attractions: ' + error.message, 'danger');
    }
  },

  renderTable(list) {
    const tbody = document.getElementById('adminTableBody');
    if (!tbody) return;

    if (!list || list.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="text-center py-4 text-muted">
            <i class="bi bi-inbox fs-2 d-block mb-1"></i> No attraction records found
          </td>
        </tr>
      `;
      return;
    }

    tbody.innerHTML = list.map(item => `
      <tr>
        <td class="text-muted fw-bold">#${item.id}</td>
        <td>
          <div class="d-flex align-items-center gap-2">
            <img src="${item.image}" class="rounded-2" style="width: 44px; height: 38px; object-fit: cover;" alt="${item.name}" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80'">
            <div>
              <strong class="text-dark d-block" style="font-size: 0.92rem;">${item.name}</strong>
              <small class="text-muted">${item.location}</small>
            </div>
          </div>
        </td>
        <td><span class="badge bg-secondary-subtle text-secondary border">${item.category}</span></td>
        <td>~${item.distance} km</td>
        <td>${item.visitingDuration} hrs</td>
        <td>
          <a href="${item.mapLink || '#'}" target="_blank" class="btn btn-sm btn-link text-primary p-0" title="View Google Maps Link">
            <i class="bi bi-box-arrow-up-right"></i> Maps
          </a>
        </td>
        <td class="text-end">
          <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-primary" onclick="AdminApp.openEditModal(${item.id})" title="Edit Attraction">
              <i class="bi bi-pencil-square"></i> Edit
            </button>
            <button class="btn btn-outline-danger" onclick="AdminApp.openDeleteModal(${item.id}, '${item.name.replace(/'/g, "\\'")}')" title="Delete Attraction">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </td>
      </tr>
    `).join('');
  },

  updateStats() {
    const totalCount = document.getElementById('statTotalCount');
    const totalCategories = document.getElementById('statTotalCategories');
    const avgDuration = document.getElementById('statAvgDuration');

    if (totalCount) totalCount.innerText = this.attractions.length;
    if (totalCategories) {
      const cats = new Set(this.attractions.map(a => a.category));
      totalCategories.innerText = cats.size;
    }
    if (avgDuration && this.attractions.length > 0) {
      const avg = this.attractions.reduce((acc, curr) => acc + (curr.visitingDuration || 0), 0) / this.attractions.length;
      avgDuration.innerText = `${avg.toFixed(1)} hrs`;
    }
  },

  openAddModal() {
    this.selectedAttractionId = null;
    const form = document.getElementById('attractionForm');
    if (form) form.reset();

    document.getElementById('attractionModalHeaderTitle').innerText = 'Add New Tourist Attraction';
    document.getElementById('attractionIdInput').value = '';
    
    // Clear validation states
    form.classList.remove('was-validated');

    const modal = new bootstrap.Modal(document.getElementById('attractionFormModal'));
    modal.show();
  },

  async openEditModal(id) {
    this.selectedAttractionId = id;
    const item = this.attractions.find(a => a.id == id);
    if (!item) return;

    document.getElementById('attractionModalHeaderTitle').innerText = `Edit Attraction: ${item.name}`;
    document.getElementById('attractionIdInput').value = item.id;
    document.getElementById('nameInput').value = item.name || '';
    document.getElementById('categoryInput').value = item.category || 'Recreation';
    document.getElementById('distanceInput').value = item.distance || '';
    document.getElementById('durationInput').value = item.visitingDuration || '';
    document.getElementById('locationInput').value = item.location || '';
    document.getElementById('imageInput').value = item.image || '';
    document.getElementById('latitudeInput').value = item.latitude || '';
    document.getElementById('longitudeInput').value = item.longitude || '';
    document.getElementById('mapLinkInput').value = item.mapLink || '';
    document.getElementById('bestTimeInput').value = item.bestTime || '';
    document.getElementById('entryFeeInput').value = item.entryFee || '';
    document.getElementById('descriptionInput').value = item.description || '';

    const form = document.getElementById('attractionForm');
    form.classList.remove('was-validated');

    const modal = new bootstrap.Modal(document.getElementById('attractionFormModal'));
    modal.show();
  },

  async saveAttraction() {
    const form = document.getElementById('attractionForm');
    if (!form.checkValidity()) {
      form.classList.add('was-validated');
      this.showToast('Please fix the highlighted validation errors.', 'warning');
      return;
    }

    const payload = {
      name: document.getElementById('nameInput').value.trim(),
      category: document.getElementById('categoryInput').value.trim(),
      distance: parseFloat(document.getElementById('distanceInput').value),
      visitingDuration: parseFloat(document.getElementById('durationInput').value),
      location: document.getElementById('locationInput').value.trim(),
      image: document.getElementById('imageInput').value.trim(),
      latitude: parseFloat(document.getElementById('latitudeInput').value) || null,
      longitude: parseFloat(document.getElementById('longitudeInput').value) || null,
      mapLink: document.getElementById('mapLinkInput').value.trim() || null,
      bestTime: document.getElementById('bestTimeInput').value.trim() || null,
      entryFee: document.getElementById('entryFeeInput').value.trim() || null,
      description: document.getElementById('descriptionInput').value.trim()
    };

    // Form data validation (FR-044, FR-045)
    if (isNaN(payload.distance) || payload.distance < 0) {
      this.showToast('Distance must be 0 or greater (km)', 'warning');
      return;
    }
    if (isNaN(payload.visitingDuration) || payload.visitingDuration <= 0) {
      this.showToast('Visiting duration must be greater than 0 hours', 'warning');
      return;
    }

    const saveBtn = document.getElementById('saveAttractionBtn');
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving...';

    try {
      if (this.selectedAttractionId) {
        await ApiService.updateAttraction(this.selectedAttractionId, payload);
        this.showToast(`Successfully updated "${payload.name}"`, 'success');
      } else {
        await ApiService.createAttraction(payload);
        this.showToast(`Successfully created "${payload.name}"`, 'success');
      }

      const modalEl = document.getElementById('attractionFormModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();

      await this.loadAttractions();
    } catch (err) {
      this.showToast('Error saving record: ' + err.message, 'danger');
    } finally {
      saveBtn.disabled = false;
      saveBtn.innerHTML = '<i class="bi bi-check-lg"></i> Save Attraction';
    }
  },

  openDeleteModal(id, name) {
    this.selectedAttractionId = id;
    document.getElementById('deleteAttractionName').innerText = name;

    const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    modal.show();
  },

  async confirmDelete() {
    if (!this.selectedAttractionId) return;

    const deleteBtn = document.getElementById('confirmDeleteBtn');
    deleteBtn.disabled = true;
    deleteBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Deleting...';

    try {
      await ApiService.deleteAttraction(this.selectedAttractionId);
      this.showToast('Attraction record deleted successfully', 'success');

      const modalEl = document.getElementById('deleteConfirmModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();

      await this.loadAttractions();
    } catch (err) {
      this.showToast('Error deleting record: ' + err.message, 'danger');
    } finally {
      deleteBtn.disabled = false;
      deleteBtn.innerHTML = '<i class="bi bi-trash"></i> Delete';
    }
  },

  async resetData() {
    if (!confirm('Are you sure you want to reset all attractions back to the default 10 Atulugama places?')) {
      return;
    }

    try {
      await ApiService.resetAttractions();
      this.showToast('Database reset to original 10 attractions', 'success');
      await this.loadAttractions();
    } catch (err) {
      this.showToast('Error resetting: ' + err.message, 'danger');
    }
  },

  showToast(message, type = 'info') {
    const container = document.getElementById('adminToastContainer');
    if (!container) return;

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-white bg-${type === 'warning' ? 'warning text-dark' : type === 'danger' ? 'danger' : type === 'success' ? 'success' : 'primary'} border-0`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');

    toastEl.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">
          ${message}
        </div>
        <button type="button" class="btn-close ${type === 'warning' ? '' : 'btn-close-white'} me-2 m-auto" data-bs-dismiss="toast"></button>
      </div>
    `;

    container.appendChild(toastEl);
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
  }
};

document.addEventListener('DOMContentLoaded', () => AdminApp.init());
