#!/usr/bin/env python3
"""
Script to fix swapped Position and Supervisor columns in employee_info.csv
Columns 5 and 6 (0-indexed) are swapped in data rows while header is correct.
"""

import csv

# File path
csv_file = r"E:\Apache Netbeans\NetbeansProjects\MotorPHms2\data\employee_info.csv"

# Read the CSV file
with open(csv_file, 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    rows = list(reader)

# Keep the header as-is
header = rows[0]
data_rows = rows[1:]

# Swap columns 5 and 6 (Position and Supervisor) in data rows
for row in data_rows:
    if len(row) > 6:  # Make sure row has enough columns
        row[5], row[6] = row[6], row[5]

# Write the corrected data back to the file
with open(csv_file, 'w', encoding='utf-8', newline='') as f:
    writer = csv.writer(f)
    writer.writerow(header)
    writer.writerows(data_rows)

print(f"✓ CSV file fixed successfully!")
print(f"✓ Swapped columns 6 and 7 (Position and Supervisor) for {len(data_rows)} data rows")
print(f"✓ Header remained unchanged")
print(f"✓ File saved: {csv_file}")
