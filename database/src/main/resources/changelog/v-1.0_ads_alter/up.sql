ALTER TABLE advertisement.apartments
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.countries
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.cities
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.regions
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.districts
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.streets
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE geo.addresses
ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

GO

ALTER TABLE advertisement.apartments
ADD COLUMN is_daily bool NOT NULL DEFAULT false;

