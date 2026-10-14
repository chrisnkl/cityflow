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
