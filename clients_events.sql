select * from clients
join contracts
on clients.client_id = contracts.client_id
join events_contracts
on events_contracts.contract_id = contracts.contract_id
join events
on events.event_id = events_contracts.event_id;