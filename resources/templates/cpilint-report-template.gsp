<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CPILint Report - ${metadata.timestamp}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            line-height: 1.5;
            background-color: #f9f9f9;
        }
        h1, h2, h3 {
            color: #0056b3;
        }
        .summary-box {
            background: #ffffff;
            padding: 20px;
            border-radius: 6px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            margin-bottom: 30px;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th {
            background: #0056b3;
            color: white;
            padding: 10px;
        }
        td {
            padding: 8px;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        pre {
            background-color: #eee;
            padding: 10px;
            border-radius: 4px;
            overflow-x: auto;
        }
        .critical {
            background-color: #ffe5e5;
        }
        .error {
            background-color: #ffeccc;
        }
        .warning {
            background-color: #ffffcc;
        }
        .info {
            background-color: #e6f2ff;
        }
        .tab-nav {
            margin-bottom: 20px;
        }
        .tab-nav button {
            padding: 10px 15px;
            margin-right: 5px;
            cursor: pointer;
            background: #ddd;
            border: none;
            border-radius: 5px 5px 0 0;
        }
        .tab-nav button.active {
            background: #ffffff;
            font-weight: bold;
        }
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        a {
            color: #0077cc;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
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
            <li><strong>Total Issues Found:</strong> ${issues.size()}</li>
            <li><strong>Command Executed:</strong> ${metadata.command}</li>
            <li><strong>Report Timestamp:</strong> ${metadata.timestamp}</li>
            <li><strong>Version:</strong> ${metadata.version}</li>
        </ul>
    </div>

    <div class="tab-nav">
        <button class="tab-btn active" onclick="showTab('issues')">Issues</button>
        <button class="tab-btn" onclick="showTab('ruleset')">Ruleset Rules</button>
        <button class="tab-btn" onclick="showTab('logs')">Logs</button>
    </div>

    <div id="issues" class="tab-content active">
        <h2>All Issues</h2>
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
                        ${issue.recommendation ?: 'N/A'}<br/>
                        <small><strong>Counter Example:</strong> ${issue.counterExample ?: 'N/A'}</small>
                    </td>
                    <td><a href="#rule-${issue.ruleClass}">${issue.ruleClass ?: 'N/A'}</a></td>
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
                    <ul>
                    <% rule.fields.each { fieldName, fieldValue -> %>
                        <li><strong>${fieldName}:</strong> <pre>${fieldValue}</pre></li>
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
        function showTab(tabId) {
            var tabs = document.getElementsByClassName('tab-content');
            var buttons = document.getElementsByClassName('tab-btn');
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].classList.remove('active');
                buttons[i].classList.remove('active');
            }
            document.getElementById(tabId).classList.add('active');
            event.target.classList.add('active');
        }
    </script>
</body>
</html>