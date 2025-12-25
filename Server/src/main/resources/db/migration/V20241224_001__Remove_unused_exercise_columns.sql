-- Migration to remove unused columns from Exercise table
-- Date: 2024-12-24
-- Description: Remove equipmentNeeded and muscleGroups columns as they are no longer used in the Android client

ALTER TABLE exercise DROP COLUMN IF EXISTS equipment_needed;
ALTER TABLE exercise DROP COLUMN IF EXISTS muscle_groups;