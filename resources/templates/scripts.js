function showTab(tabId) {
    const tabs = document.getElementsByClassName('tab-content');
    const buttons = document.getElementsByClassName('tab-btn');
    for (let i = 0; i < tabs.length; i++) {
        tabs[i].classList.remove('active');
        buttons[i].classList.remove('active');
    }
    document.getElementById(tabId).classList.add('active');
    event.target.classList.add('active');
    localStorage.setItem('selectedTab', tabId);
}

window.onload = function () {
    const tabId = localStorage.getItem('selectedTab') || 'issues';
    const tabBtn = document.querySelector(`.tab-btn[onclick="showTab('${tabId}')"]`);
    if (tabBtn) tabBtn.click();
}
function filterTable() {
    const iflowVal = document.getElementById('filter-iflow').value.toLowerCase();
    const severityVal = document.getElementById('filter-severity').value.toLowerCase();
    const textVal = document.getElementById('filter-text').value.toLowerCase();

    const rows = document.querySelectorAll('#issues table tbody tr');
    rows.forEach(row => {
        const iflow = row.children[0].innerText.toLowerCase();
        const severity = row.children[1].innerText.toLowerCase();
        const message = row.children[2].innerText.toLowerCase();
        const recommendation = row.children[3].innerText.toLowerCase();
        const rule = row.children[4].innerText.toLowerCase();

        const matches =
            (!iflowVal || iflow.includes(iflowVal)) &&
            (!severityVal || severity.includes(severityVal)) &&
            (!textVal || message.includes(textVal) || rule.includes(textVal) || recommendation.includes(textVal));

        row.style.display = matches ? '' : 'none';
    });
}
function resetFilters() {
    document.getElementById('filter-iflow').value = '';
    document.getElementById('filter-severity').value = '';
    document.getElementById('filter-text').value = '';
    filterTable();
}
function toggleDetails(el) {
    var box = el.nextElementSibling;
    if (box.style.display === "block") {
        box.style.display = "none";
        el.innerText = "Show Recommendation";
    } else {
        box.style.display = "block";
        el.innerText = "Hide Recommendation";
    }
}
function toggleRuleField(el) {
    const content = el.nextElementSibling;
    const isVisible = content.style.display === "block";
    content.style.display = isVisible ? "none" : "block";
    el.innerText = isVisible ? "Show" : "Hide";
}
document.querySelectorAll('th').forEach(header => {
    header.addEventListener('click', () => {
        const table = header.closest('table');
        const rows = Array.from(table.querySelectorAll('tbody > tr'));
        const index = Array.from(header.parentElement.children).indexOf(header);
        const sortedRows = rows.sort((a, b) => {
            const cellA = a.children[index].textContent.trim().toLowerCase();
            const cellB = b.children[index].textContent.trim().toLowerCase();
            return cellA.localeCompare(cellB);
        });
        table.querySelector('tbody').append(...sortedRows);
    });
});