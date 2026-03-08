#!/usr/bin/env python3
"""Fix employee_logins.csv by removing malformed quoted entries"""

csv_path = r"E:\Apache Netbeans\NetbeansProjects\MotorPHms2\data\employee_logins.csv"

# Read all lines
with open(csv_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Keep only the first 36 lines (header + 35 data rows, removes the malformed 10036 & 10037)
corrected_lines = lines[0:36]

# Write back
with open(csv_path, 'w', encoding='utf-8') as f:
    f.writelines(corrected_lines)

print("✓ Fixed CSV - removed malformed entries with extra quotes (10036 and 10037)")
