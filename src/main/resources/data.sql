-- Sample menu data for the College Canteen Ordering System.
-- Runs on every startup (spring.sql.init.mode=always). Guarded with
-- INSERT IGNORE against the unique constraint on `name` (declared on the
-- MenuItem entity itself, so Hibernate creates it once via ddl-auto=update)
-- so re-running the app won't create duplicate rows.

INSERT INTO menu_items (name, description, price, category, available, image_url) VALUES
('Masala Dosa', 'Crispy rice crepe filled with spiced potato filling, served with sambar & chutney', 60.00, 'Meals', true, NULL),
('Veg Puff', 'Flaky pastry filled with spiced mixed vegetables', 25.00, 'Snacks', true, NULL),
('Samosa', 'Deep-fried pastry with a savoury potato and pea filling', 20.00, 'Snacks', true, NULL),
('Veg Sandwich', 'Grilled sandwich with fresh vegetables and mint chutney', 40.00, 'Snacks', true, NULL),
('Chicken Roll', 'Spiced chicken wrapped in a soft paratha', 70.00, 'Meals', true, NULL),
('Paneer Butter Masala with Rice', 'Cottage cheese in a rich tomato gravy, served with steamed rice', 90.00, 'Meals', true, NULL),
('Veg Biryani', 'Fragrant basmati rice cooked with mixed vegetables and spices', 80.00, 'Meals', true, NULL),
('Masala Chai', 'Hot Indian spiced tea', 15.00, 'Beverages', true, NULL),
('Filter Coffee', 'South Indian style filter coffee', 20.00, 'Beverages', true, NULL),
('Cold Coffee', 'Chilled blended coffee with ice cream', 45.00, 'Beverages', true, NULL),
('Fresh Lime Soda', 'Refreshing lime juice with soda', 30.00, 'Beverages', true, NULL),
('Gulab Jamun (2 pcs)', 'Soft milk dumplings soaked in sugar syrup', 35.00, 'Desserts', true, NULL),
('Ice Cream Cup', 'Vanilla ice cream cup', 30.00, 'Desserts', true, NULL),
('French Fries', 'Crispy salted potato fries', 50.00, 'Snacks', true, NULL),
('Egg Puff', 'Flaky pastry with spiced boiled egg filling', 30.00, 'Snacks', false, NULL);