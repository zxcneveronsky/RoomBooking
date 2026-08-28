const API = window.location.protocol + '//' + window.location.host + '/api/v1';

function safeFloat(val) {
    const n = parseFloat(val);
    return isNaN(n) ? 0 : n;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function setAuthEmail(email) {
    if (email) localStorage.setItem('email', email);
}

function clearToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
}

function isAuth() {
    return !!getToken();
}

function getUserId() {
    try {
        const token = getToken();
        if (!token) return null;
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId || payload.sub || null;
    } catch (e) { return null; }
}

function getUserRole() {
    try {
        const token = getToken();
        if (!token) return null;
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.role || null;
    } catch (e) { return null; }
}

function isAdmin() {
    return getUserRole() === 'ADMIN';
}

async function request(path, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`${API}${path}`, { ...options, headers });
    if (res.status === 401 || res.status === 403) {
        clearToken();
        window.location.href = '/login.html';
        return null;
    }
    if (res.status === 204) return null;
    if (!res.ok) {
        const err = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(err.message || 'Ошибка запроса');
    }
    const ct = res.headers.get('content-type') || '';
    if (!ct.includes('application/json')) return null;
    return res.json();
}

function logout() {
    clearToken();
    window.location.href = '/index.html';
}

function pad2(n) {
    return String(n).padStart(2, '0');
}

function todayLocal() {
    const d = new Date();
    return d.getFullYear() + '-' + pad2(d.getMonth() + 1) + '-' + pad2(d.getDate());
}

function nowLocalISO() {
    const d = new Date();
    return todayLocal() + 'T' + pad2(d.getHours()) + ':' + pad2(d.getMinutes()) + ':' + pad2(d.getSeconds());
}

// Дата+время из input[type=datetime-local] -> "YYYY-MM-DDTHH:mm:ss" для бэкенда
function toBackendDateTime(value) {
    if (!value) return null;
    return value.length === 16 ? value + ':00' : value;
}

// Date -> "YYYY-MM-DDTHH:mm" для значения input[type=datetime-local]
function toDateTimeInput(d) {
    const date = (d instanceof Date) ? d : new Date(d);
    return date.getFullYear() + '-' + pad2(date.getMonth() + 1) + '-' + pad2(date.getDate())
        + 'T' + pad2(date.getHours()) + ':' + pad2(date.getMinutes());
}

// 'YYYY-MM-DD' из Date | string
function formatDate(date) {
    if (!date) return todayLocal();
    if (typeof date === 'string') return date.split('T')[0];
    const d = new Date(date);
    return d.getFullYear() + '-' + pad2(d.getMonth() + 1) + '-' + pad2(d.getDate());
}

function formatDateTime(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleString('ru-RU', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
}

function formatTimeRange(startIso, endIso) {
    const s = new Date(startIso);
    const e = new Date(endIso);
    if (isNaN(s.getTime()) || isNaN(e.getTime())) return '—';
    const sameDay = formatDate(s) === formatDate(e);
    const time = s.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) + ' – '
        + e.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });
    if (sameDay) return s.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' }) + ', ' + time;
    return formatDateTime(startIso) + ' – ' + formatDateTime(endIso);
}

// --- Кэш опций (в бэкенде нет отдельного эндпоинта списка опций) ---
const optionCache = new Map();

function indexOptions(rooms) {
    if (!rooms) return;
    rooms.forEach(r => {
        (r.options || []).forEach(o => { if (o && o.id) optionCache.set(o.id, o.name); });
    });
}

function getAllOptions() {
    return Array.from(optionCache.entries()).map(([id, name]) => ({ id, name }));
}

// --- Навигация ---
function buildPublicNav() {
    const root = document.getElementById('appNav');
    if (!root) return;
    root.innerHTML = `
        <nav class="glass-nav fixed top-0 w-full z-50 h-16 flex items-center px-4 md:px-10 border-b border-gray-100/50">
            <a href="/index.html" class="font-extrabold text-xl tracking-tighter">Room<span class="text-blue-600">Booking</span></a>
        </nav>`;
}

function buildAppNav(active) {
    const root = document.getElementById('appNav');
    if (!root) return;
    const link = (href, label, key) =>
        `<a href="${href}" class="text-sm font-semibold ${active === key ? 'text-blue-600' : 'text-gray-600 hover:text-black'}">${label}</a>`;
    const email = localStorage.getItem('email') || '';
    root.innerHTML = `
        <nav class="glass-nav fixed top-0 w-full z-50 h-16 flex items-center px-4 md:px-10 border-b border-gray-100/50">
            <div class="w-1/2 lg:w-1/4">
                <a href="/dashboard.html" class="font-extrabold text-xl tracking-tighter">Room<span class="text-blue-600">Booking</span></a>
            </div>
            <div class="hidden lg:flex flex-1 justify-center overflow-hidden px-4">
                <p class="text-[10px] font-black uppercase tracking-[0.4em] text-black whitespace-nowrap opacity-50">Бронирование переговорных</p>
            </div>
            <div class="w-1/2 lg:w-1/4 flex justify-end items-center gap-5">
                ${link('/rooms.html', 'Переговорные', 'rooms')}
                <span id="nav-email" class="hidden sm:block text-xs font-bold text-gray-400 truncate max-w-[150px]">${escapeHtml(email)}</span>
                <button onclick="logout()" class="bg-gray-100 text-gray-700 text-sm font-bold px-4 py-2 rounded-xl hover:bg-gray-200">Выйти</button>
            </div>
        </nav>`;
}

// --- Пагинация (Spring Page: content, totalPages, number, totalElements) ---
function renderPagination(containerId, data, fn) {
    const el = document.getElementById(containerId);
    if (!el) return;
    const total = data.totalPages || 1;
    const current = data.number || 0;
    if (total <= 1) { el.innerHTML = ''; return; }
    const name = fn.name;
    let html = '';
    html += `<button onclick="${name}(${current - 1})" class="px-3 py-1 rounded-lg border ${current === 0 ? 'opacity-30 cursor-not-allowed' : 'hover:bg-gray-100'}">←</button>`;
    for (let i = Math.max(0, current - 2); i < Math.min(total, current + 3); i++) {
        html += `<button onclick="${name}(${i})" class="px-3 py-1 rounded-lg ${i === current ? 'bg-blue-600 text-white' : 'border hover:bg-gray-100'}">${i + 1}</button>`;
    }
    html += `<button onclick="${name}(${current + 1})" class="px-3 py-1 rounded-lg border ${current >= total - 1 ? 'opacity-30 cursor-not-allowed' : 'hover:bg-gray-100'}">→</button>`;
    el.innerHTML = html;
}

// --- Общие стили (совпадают с fitness-assistant) ---
const BASE_STYLES = `
    body { font-family: 'Inter', sans-serif; background: #ffffff; color: #1d1d1f; overflow-x: hidden; }
    .hero-title { font-family: 'Manrope', sans-serif; letter-spacing: -0.04em; line-height: 0.9; }
    .glass-nav { background: rgba(255, 255, 255, 0.8); backdrop-filter: saturate(180%) blur(20px); }
    .btn-apple { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); border-radius: 1.25rem; }
    .btn-apple:hover { transform: translateY(-3px) scale(1.02); box-shadow: 0 20px 40px rgba(0,0,0,0.1); }
    .dash-card { transition: all 0.5s cubic-bezier(0.15, 0.83, 0.66, 1); border-radius: 2.2rem; border: 1px solid #f2f2f7; background: #ffffff; }
    .dash-card:hover { transform: translateY(-4px); box-shadow: 0 20px 50px rgba(0,0,0,0.05); }
    .feature-card { transition: all 0.5s cubic-bezier(0.15, 0.83, 0.66, 1); border-radius: 2.2rem; border: 1px solid #f2f2f7; background: rgba(245,245,247,0.4); }
    .feature-card:hover { transform: translateY(-8px); background: #fff; box-shadow: 0 30px 60px rgba(0,0,0,0.06); border-color: #0071e3; }
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px); z-index: 100; display: none; align-items: center; justify-content: center; padding: 1rem; }
    .modal-overlay.open { display: flex; }
    .modal-box { border-radius: 2rem; background: #fff; max-width: 520px; width: 100%; padding: 2rem; box-shadow: 0 40px 80px rgba(0,0,0,0.15); max-height: 90vh; overflow-y: auto; }
    .auth-card { transition: all 0.5s cubic-bezier(0.15, 0.83, 0.66, 1); border-radius: 2.2rem; border: 1px solid #f2f2f7; background: #ffffff; }
    .auth-input { transition: all 0.2s ease; }
    .auth-input:focus { border-color: #0071e3; box-shadow: 0 0 0 4px rgba(0,113,227,0.08); }
    .input-base { transition: all 0.2s ease; }
    .input-base:focus { border-color: #0071e3; box-shadow: 0 0 0 4px rgba(0,113,227,0.08); }
    .chip { font-size: 0.7rem; font-weight: 700; padding: 0.25rem 0.7rem; border-radius: 9999px; background: #eff6ff; color: #2563eb; display: inline-block; }
    .tab-btn { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
`;

(function injectBaseStyles() {
    if (document.getElementById('rb-base-styles')) return;
    const style = document.createElement('style');
    style.id = 'rb-base-styles';
    style.textContent = BASE_STYLES;
    document.head.appendChild(style);
})();
