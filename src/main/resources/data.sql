INSERT IGNORE INTO subscription_plan (name, plan_type, description, price, duration_in_days, max_events, max_participants_per_event, features, active)
VALUES
('Free',       'FREE',       'Basic access with limited features',       0.00,   0,   2,   50,   'Create up to 2 events,50 participants per event,Email support',  true),
('Basic',      'BASIC',      'For small event organizers',               9.99,   30,  10,  200,  'Create up to 10 events,200 participants,Email support,Basic analytics',  true),
('Premium',    'PREMIUM',    'For professional event organizers',        29.99,  30,  50,  1000, 'Up to 50 events,1000 participants,Priority support,Advanced analytics',  true),
('Enterprise', 'ENTERPRISE', 'Unlimited access for large organizations', 99.99,  30,  -1,  -1,   'Unlimited events,Unlimited participants,24/7 support,API access',  true);