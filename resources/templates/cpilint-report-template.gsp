<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CPILint Report - ${metadata.timestamp}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            line-height: 1.2;
            background-color: #f9f9f9;
            font-size: 14px;
        }
        h1, h2, h3 {
            color: #0056b3;
        }
        /* h3 {
            color: #B41601;
        } */
        .summary-box {
            background: #fff;
            padding: 12px 20px;
            border-radius: 6px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            margin-bottom: 30px;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 6px 8px;
            vertical-align: top;
            text-align: left;
        }
        pre {
            background-color: #eee;
            padding: 4px;
            border-radius: 3px;
            overflow-x: auto;
        }
        th {
            background: #0056b3;
            color: white;
            cursor: pointer;
        }
        tr:nth-child(even) {
            background-color: #f7f7f7;
        }
        .critical {
            background-color: #ffe5e5;
        }
        .error {
            background-color: #fff3cd;
        }
        .warning {
            background-color: #fffdd0;
        }
        .info {
            background-color: #e6f2ff;
        }
        .recommendation-toggle {
            cursor: pointer;
            color: #0056b3;
            text-decoration: underline;
        }
        .recommendation-details {
            display: none;
            margin-top: 5px;
            background: #f2f2f2;
            padding: 5px;
            border-left: 3px solid #ccc;
        }
        .tab-nav {
            margin-bottom: 20px;
        }
        .tab-nav button {
            padding: 9px 12px;
            margin-right: 5px;
            cursor: pointer;
            background: #d3d3d3;
            border: none;
            border-radius: 3px 3px 0 0;
        }
        .tab-nav button.active {
            background: #B41601;
            font-weight:bold;
            color: #fff;
            transition: all .2s ease-out;
        }
        .tab-nav button:hover {
            box-shadow: #6d6d6d 1px 1px 3px;
            transition: all .1s ease-in-out;
        }
        .tab-content {
            display: none;
            opacity: 0;
            transition: all .2s ease-out;
        }
        .tab-content.active {
            display: block;
            opacity: 1;
            transition: all .2s ease-out;
        }
        a {
            color: #0077cc;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .rule-field-toggle {
            cursor: pointer;
            color: #0056b3;
            font-size: 13px;
            padding: 2px;
            text-decoration: underline;
        }
        .rule-field-content {
            display: none;
            margin: 3px 0 8px 0;
            padding: 5px;
            background: #f0f0f0;
            border-left: 3px solid #ccc;
            white-space: pre-wrap;
            font-size: 13px;
        }
    </style>
</head>
<body>
    <h1>CPILint Governance Report</h1>

    <div class="summary-box">
        <h2>Summary</h2>
        <ul>
            <li><strong>Package ID:</strong> ${metadata.packageId}</li>
            <li><strong>Ruleset:</strong> ${metadata.ruleset}</li>
            <li><strong>Total CPILint Issues:</strong> ${cpilintIssues?.size() ?: 0}</li>
            <li><strong>Total CodeNarc Violations:</strong> ${codenarcViolations?.size() ?: 0}</li>
            <li>
                <strong>Command Executed:</strong>
                <code class="bash">${metadata.command}</code>
            </li>
            <li><strong>Report Timestamp:</strong> ${metadata.timestamp}</li>
            <li><strong>Version:</strong> ${metadata.version}</li>
        </ul>
    </div>

    <div class="tab-nav">
        <button class="tab-btn active" onclick="showTab('issues', event)">Issues</button>
        <button class="tab-btn" onclick="showTab('codenarc', event)">CodeNarc</button>
        <button class="tab-btn" onclick="showTab('ruleset', event)">Ruleset Rules</button>
        <button class="tab-btn" onclick="showTab('logs', event)">Logs</button>
    </div>

    <div id="issues" class="tab-content active">
        <h2>All Issues</h2>
        <div style="margin-bottom: 10px;">
            <input id="filter-iflow" type="text" placeholder="Filter by iFlow..." oninput="filterTable()" style="margin-right: 5px;">
            
            <select id="filter-severity" onchange="filterTable()" style="margin-right: 5px;">
                <option value="">All Severities</option>
                <option value="critical">Critical</option>
                <option value="error">Error</option>
                <option value="warning">Warning</option>
                <option value="info">Info</option>
            </select>
            
            <input id="filter-text" type="text" placeholder="Search message or rule..." oninput="filterTable()" style="margin-right: 5px;">

            <button onclick="resetFilters()">Reset</button>
        </div>
        <table>
            <thead>
                <tr>
                    <th>iFlow</th>
                    <th>Severity</th>
                    <th>Message</th>
                    <th>Recommendation</th>
                    <th>Rule</th>
                </tr>
            </thead>
            <tbody>
                <% issues.each { issue -> %>
                <tr class="${(issue.severity ?: '').toLowerCase()}">
                    <td>${issue.iflowId ?: 'N/A'}</td>
                    <td>${issue.severity}</td>
                    <td>${issue.message}</td>
                    <td>
                        <span class="recommendation-toggle" onclick="toggleDetails(this)">
                            Show Recommendation
                        </span>
                        <div class="recommendation-details">
                            ${issue.recommendation ?: 'N/A'}<br/>
                            <small><strong>Counter Example:</strong> ${issue.counterExample ?: 'N/A'}</small>
                        </div>
                    </td>
                    <td><a href="#rule-${issue.ruleClass}">${issue.ruleClass ?: 'N/A'}</a></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <div id="codenarc" class="tab-content">
        <h2>CodeNarc Violations</h2>
        <table>
            <thead>
                <tr>
                    <th>iFlow</th>
                    <th>File</th>
                    <th>Rule</th>
                    <th>Priority</th>
                    <th>Line</th>
                    <th>Message</th>
                </tr>
            </thead>
            <tbody>
            <% if (reportData?.codenarcResults?.packages) { %>
                <% reportData.codenarcResults.packages.each { pkg -> 
                    if (pkg?.files) {
                        pkg.files.each { file ->
                            if (file?.violations) {
                                file.violations.each { v -> %>
                <tr>
                    <% // derive iFlow from the file path, e.g. first segment %>
                    <td><%= file.name.tokenize('/\\')[0] %></td>
                    <td><%= file.name %></td>
                    <td><%= v.ruleName %></td>
                    <td><%= v.priority %></td>
                    <td><%= v.lineNumber %></td>
                    <td><%= v.message %></td>
                </tr>
                <%          }
                            }
                        }
                    }
                } %>
            <% } else { %>
                <tr>
                    <td colspan="6">No CodeNarc results available or no violations found.</td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <div id="ruleset" class="tab-content">
        <h2>Ruleset and Violations</h2>
        <p>Below are the defined rules and whether they were violated in this report.</p>
        <table>
            <thead>
                <tr>
                    <th>Rule</th>
                    <th>Violated?</th>
                    <th>Example</th>
                    <th>Rationale</th>
                </tr>
            </thead>
            <tbody>
            <% rules.each { rule -> 
                def isViolated = violatedRuleIds.any { violation -> violation?.startsWith(rule.ruleId) }
            %>
            <tr id="rule-${rule.ruleId}">
                <td>${rule.ruleId}</td>
                <td>
                    <% if (isViolated) { %>
                        <strong style="color:red;">Yes</strong>
                    <% } else { %>
                        No
                    <% } %>
                </td>
                <td>
                    <ul style="margin: 0; padding-left: 16px;">
                        <% rule.fields.each { fieldName, fieldValue -> 
                            def isVerbose = ['or', 'scheme', 'not'].contains(fieldName)
                        %>
                            <li>
                                <strong>${fieldName}:</strong>
                                <% if (isVerbose) { %>
                                    <div class="rule-field-toggle" onclick="toggleRuleField(this)">Show</div>
                                    <div class="rule-field-content">${fieldValue}</div>
                                <% } else { %>
                                    <pre style="margin: 2px 0;">${fieldValue}</pre>
                                <% } %>
                            </li>
                        <% } %>
                    </ul>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <div id="logs" class="tab-content">
        <h2>Console Logs</h2>
        <pre>${consoleLogs}</pre>
    </div>

<script>
function showTab(tabId, e) {
    try {
        // Use the event parameter passed to the function instead of the global event
        const evt = e || window.event;
        const target = evt?.currentTarget || evt?.target;
        
        const tabs = document.getElementsByClassName('tab-content');
        const buttons = document.getElementsByClassName('tab-btn');
        for (let i = 0; i < tabs.length; i++) {
            tabs[i].classList.remove('active');
            buttons[i].classList.remove('active');
        }
        document.getElementById(tabId).classList.add('active');
        if (target) {
            target.classList.add('active');
        }
        localStorage.setItem('selectedTab', tabId);
    } catch (e) {
        console.error("Error in showTab:", e);
    }
}

window.onload = function () {
    const tabId = localStorage.getItem('selectedTab') || 'issues';
    const tabBtn = document.querySelector(`.tab-btn[onclick="showTab('${tabId}', event)"]`);
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

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('th').forEach(header => {
        header.addEventListener('click', () => {
            const table = header.closest('table');
            const rows = Array.from(table.querySelectorAll('tbody > tr'));
            const index = Array.from(header.parentElement.children).indexOf(header);
            const sortedRows = rows.sort((a, b) => {
                const cellA = a.children[index]?.textContent.trim().toLowerCase() || '';
                const cellB = b.children[index]?.textContent.trim().toLowerCase() || '';
                return cellA.localeCompare(cellB);
            });
            table.querySelector('tbody').append(...sortedRows);
        });
    });
});
</script>
</body>
</html>