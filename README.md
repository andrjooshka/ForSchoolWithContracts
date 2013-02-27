Hi!
This is resource management and accounting application for those organisations who sell services and products by the subsctiption.
So far it allows to do the following:
Account schools, teachers, contracts, clients, event types (subjects, type of services or products), events.
Calculate return and teachers salaries.
Schedules for teachers and contracts.
Automatical event planning.
Nice visual calendar to see teacher's schedule.

Quickly calculate statistics to know:
The most or least profitable teacher, client, school, event type.

Archictecture is in trasit from bad MVC to good MVC.
I'm also working on documentation and adding comments.
At start it was very simple application and controller logic had been hosted inside the pages. Now all new logic is hosted inside Mediators (tap.execounting.dal.mediators) and their interfaces. Pages should only fire actions on this mediators. However the view logic of the pages is frequently not hosted inside mediators.
Do this -- and this will be clean MVC application.