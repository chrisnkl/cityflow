INSERT INTO categories (id, name, description, tip, enabled, radius) VALUES
    (
        '30000000-0000-0000-0000-000000000001',
        'Blocked sidewalk or accessibility ramp',
        'A sidewalk or accessibility ramp that is blocked or inaccessible.',
        'Identify the location and, if possible, include a photo of the obstruction.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000002',
        'Obstruction on a cycle path or micromobility lane',
        'An obstruction that makes travel difficult or impossible on a cycle path or micromobility lane.',
        'Describe the obstruction and the direction of travel affected.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000003',
        'Pothole, road damage, or hazard to pedestrians/cyclists',
        'A pothole, damaged road surface, or other hazard that puts pedestrians or cyclists at risk.',
        'If possible, include a photo and describe the size of the damage or the hazard.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000004',
        'Temporary roadworks or road closure',
        'Roadworks or a temporary closure affecting traffic or accessibility.',
        'Include the road, expected duration if known, and any available detour.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000005',
        'Faulty traffic signal or pedestrian crossing',
        'A traffic signal or pedestrian crossing that is damaged or not working correctly.',
        'Describe the fault and identify the intersection or nearest landmark.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000006',
        'Major delay or overcrowding at a public transport stop/station',
        'A major delay or excessive crowding at a public transport stop or station.',
        'Include the route, stop or station, and time of the incident.',
        TRUE,
        5.0
    ),
    (
        '30000000-0000-0000-0000-000000000007',
        'Other accessibility or mobility issue',
        'An accessibility or mobility issue not covered by the other categories.',
        'Describe the issue and how it affects travel.',
        TRUE,
        5.0
    )
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    tip = EXCLUDED.tip,
    enabled = EXCLUDED.enabled,
    radius = EXCLUDED.radius;

-- Demo data is enabled only by the development Spring profile.
DO $seed$
BEGIN
    IF '${seedDemoData}' = 'true' THEN
        INSERT INTO permissions (id, code) VALUES
            ('00000000-0000-0000-0000-000000000001', 'REPORT_ISSUE'),
            ('00000000-0000-0000-0000-000000000002', 'RECEIVE_NOTIFICATIONS'),
            ('00000000-0000-0000-0000-000000000003', 'VIEW_PROFILE'),
            ('00000000-0000-0000-0000-000000000004', 'VIEW_OWN_REPORTS'),
            ('00000000-0000-0000-0000-000000000005', 'VIEW_STATISTICS'),
            ('00000000-0000-0000-0000-000000000006', 'VIEW_ALL_REPORTS'),
            ('00000000-0000-0000-0000-000000000007', 'VALIDATE_REPORT'),
            ('00000000-0000-0000-0000-000000000008', 'REJECT_REPORT'),
            ('00000000-0000-0000-0000-000000000009', 'UPDATE_REPORT_STATUS'),
            ('00000000-0000-0000-0000-000000000010', 'MANAGE_SYSTEM_SETTINGS'),
            ('00000000-0000-0000-0000-000000000011', 'MANAGE_USERS'),
            ('00000000-0000-0000-0000-000000000012', 'MANAGE_CATEGORIES');

        INSERT INTO roles (id, name, created_at, updated_at) VALUES
            ('10000000-0000-0000-0000-000000000001', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            ('10000000-0000-0000-0000-000000000002', 'ROLE_EMPLOYEE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
            ('10000000-0000-0000-0000-000000000003', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

        INSERT INTO role_permissions (role_id, permission_id)
        SELECT '10000000-0000-0000-0000-000000000001', id FROM permissions;

        INSERT INTO role_permissions (role_id, permission_id)
        SELECT '10000000-0000-0000-0000-000000000002', id
        FROM permissions
        WHERE code IN (
            'RECEIVE_NOTIFICATIONS',
            'VIEW_PROFILE',
            'VIEW_OWN_REPORTS',
            'VIEW_STATISTICS',
            'VIEW_ALL_REPORTS',
            'VALIDATE_REPORT',
            'REJECT_REPORT',
            'UPDATE_REPORT_STATUS'
        );

        INSERT INTO role_permissions (role_id, permission_id)
        SELECT '10000000-0000-0000-0000-000000000003', id
        FROM permissions
        WHERE code IN (
            'REPORT_ISSUE',
            'RECEIVE_NOTIFICATIONS',
            'VIEW_PROFILE',
            'VIEW_OWN_REPORTS'
        );

        INSERT INTO users (id, email, password, created_at, updated_at) VALUES
            (
                '20000000-0000-0000-0000-000000000001',
                'admin@gmail.com',
                '$2a$12$LAQWV2pDTVlq9jzWcfxlwuaaVDQBHR5IUZZpPevJM.Ai5EXBwFLWK',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            ),
            (
                '20000000-0000-0000-0000-000000000002',
                'employee@gmail.com',
                '$2a$12$FZ9iyJeCUeHuyAUdxajUA.uL/Ul4StEWVQT.nu91FOGYOOUxlN7h6',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            ),
            (
                '20000000-0000-0000-0000-000000000003',
                'user@gmail.com',
                '$2a$12$eth2Y6rGI4jPQJ4ymAqQq.QwW6YelfKoM/Wf44kC.JlPWBv3HyNvi',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            );

        INSERT INTO user_roles (user_id, role_id) VALUES
            ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001'),
            ('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002'),
            ('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003');

        INSERT INTO reports (
            id,
            category_id,
            status,
            latitude,
            longitude,
            comments,
            photo_url,
            priority,
            created_at,
            updated_at,
            created_by,
            updated_by
        ) VALUES (
            '40000000-0000-0000-0000-000000000001',
            '30000000-0000-0000-0000-000000000003',
            'PENDING',
            37.9838,
            23.7275,
            'Development sample report for road damage.',
            NULL,
            1.0,
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP,
            '20000000-0000-0000-0000-000000000003',
            '20000000-0000-0000-0000-000000000003'
        );

        INSERT INTO notifications (id, report_id, created_at, updated_at) VALUES (
            '50000000-0000-0000-0000-000000000001',
            '40000000-0000-0000-0000-000000000001',
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
        );
    END IF;
END;
$seed$;