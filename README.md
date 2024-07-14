## Implementation details

## My Approach

### Planning

##### Assumptions

Studio Ghibli is an animation studio that creates films and other media [1](https://en.wikipedia.org/wiki/Studio_Ghibli).
They currently have about 22 films [2](https://ghibliapi.vercel.app/films). As a movie studio that is still running,
and has been for 38 years, there is the possibility their movie catalog will grow to be large, especially with the
knowledge of thousands of animes and millions of movies in the world today. So I will be building my solution
on the premise of a large dataset.

##### API documentation review
I took special note of the relationships of entities and the amount of entities that can be returned in a response.
I also noticed there are no pagination properties, so I had to specify a limit (the max limit) to prevent any missing
data on the app. For example, there are 57 people and if I did not specify a limit, only 50 people would have been
returned, as 50 is said to be the default limit. However, while testing, I noticed the default limit was not applied 
as it says in the documentation, but I still left my implementation the same way because if this were some API bug or
the limit is enforced in the future, this application would not be affected.

Note that while the absence of pagination works for now since the data set is relatively small, the API will definitely
need it as the dataset grows.

I also removed unused API classes and properties to improve performance (by reducing the number of properties required
to be parsed), code cleanness & clarity.

##### Tasks

###### First task - listing people in a film as part of the film details view
For the first task, which involved listing people in a film as part of the film details view, I noticed it required
getting data from more than one entity. Since it is cleaner to have a data layer method retrieve a single entity
and with my aim to hold onto the single responsibility principle, I considered using use cases rather than view models or
repositories to process the data required for the UI i.e. use cases calling two repository methods, one to get the film
and another to get the people in the film, and combining both to what the UI requires. Scanning through other tasks, I
also saw the need for use cases, and this drove my architecture decision to include a use case layer.

Also, I noticed the model used in the data layer was also used in the UI. From experience, this can create a lot of
issues/bugs, and having separate models helps things be more maintainable over time. Hence, I began the process of having
different data models for each layer (in this case, UI and repository).

###### Second task - creating a way to mark films as favorites
For the second task, which involved creating a way to mark films as favorites, my first idea was to persist the film IDs
a user had selected to be their favorite in some local storage. Then when loading the list of films from the API to
display on the screen, I match them to the persisted favorite film IDs before displaying them to the user. In this case,
I planned to use a local storage system for simple datasets like Jetpack Datastore. This idea is good, but I believe a
better experience for users will be the ability to access the contents of the application even when they are offline. 
Since the assumption made earlier was that the dataset of films could be large, it would be inefficient to persist all 
films and their related information on users' devices, especially because they would likely only be interested in a small 
subset of the large dataset of films. However, a user marking a film as a favorite means they are interested in it, and 
these are good candidates to be persisted locally for offline access.

Since the dataset entities (films and people) had a well-defined structure with clear relationships, and since I had to 
perform some database transactions, I decided to use an SQL database. I implemented different tables for these entities
based on the structure of their relationships, and ensured that when adding or removing a film as a favorite, its 
corresponding `people` entity needed to the available and updated accordingly for the process to succeed, otherwise, 
I notify the user of the particular error that occurred.

On the UI layer, I improved the user experience in failure or offline situations by providing a way for them to reload
data and the option to view the films they marked as favorites. When internet connectivity returns, they can view all
films again.

Also, there was the possibility of having several network calls made to the same endpoints (e.g. `/films/`) within the
same application session. For instance, if one goes to the home page several times within the same application instance.
To make this more efficient, I introduced a cache, where I get data from. The idea is that the first call goes to the API,
the result is cached, and future calls get the data from the cache. The cache does not survive across application instances,
so we are avoiding persisting unnecessary data for users. The cache also does not hold stale data because the movie dataset
does not change (they are records), and in the unlikely event that it does (say a mistake was corrected), new application
instances will get data from the API since the cache would be empty in this situation.

###### Third task - filtering films by species and favorite
For the last set of tasks that involved filtering films by species and favorite, I also felt that a better user experience
would be to remember a user's selected filters across application instances. Hence, I decided to persist user-selected
filters in a local storage. I also saw this as a good thing as it would mean that the data layer is what is
solely responsible for housing the data of the application.

Last but not least, I planned to refactor the existing `filterFilms` method from the repository to a use case class to ensure
each class has a single responsibility, aiming to make this application cleaner and maintainable.

##### Tests
I decided to write tests for all layers to ensure proper functionality and to prevent regressions with future changes.
Having this in mind directed how I wrote the code. For example, ensuring dependencies are injected into classes, having
classes extend an interface to allow the provision of test implementations when needed, and extracting UI components to 
prevent them from being tied to the platform dependencies.

### Implementation

#### Architecture
I employed the concepts of Clean Architecture in a way that promoted a uni-directional flow pattern which makes it easy to
go through the code.

```
UI <-> ViewModel <-> Use case <-> Repository <-> Data sources (API, Database, Cache).
```

#### Separation of concerns
For better code maintenance and clarity, I ensured each layer in the architecture had a single responsibility.
The following layers in the codebase serve the following purpose:
- UI: Displays data on the screen.
- ViewModel: State holders for the UI.
- Use case: Application/business logic.
- Repository: Access to the data sources. Determines which data source to retrieve data from.
- API - Remote data source.
- Database - Persisting user-specified data (favorite films/people data and filters).
- Cache - Persisting responses from the API to prevent multiple calls to the API within an application instance/session.

With this, I made some updates to the existing code base:
1. Differentiate data layer models from UI models.
2. Extract the `filterFilms` method from the repository and place it in a use case.
3. Persist filters in the data layer.

###### UI
- I used for Jetpack Compose (Android) and SwiftUI (iOS).
- To ensure `Snackbar` messages are always shown, I employed a strategy that made each `Snackbar` feedback time-based which
  ensured the recomposition of Jetpack Compose components.

###### ViewModel
- I used Kotlin Flow to react to updates in the data layer and ensure the UI data is up-to-date.

###### Use case
- I used `Dispatcher.Default` which is recommended for computational tasks.
- I leverage Kotlin coroutine's `async` function to get a list of persons in parallel for faster computation.
- Going through this layer in some cases can feel like a disadvantage when it requires a simple 
function call like where it is solely used to observe changes in the data layer. An alternate approach I had in mind
was to move this layer from being in between the view model and the repository to being a layer that both the view model 
and repository depend on, but this would have disrupted the uni-directional flow pattern, which might make the code 
a little bit more difficult to navigate. Since most use cases were not simple functions, in addition to the advantages 
of uni-directional flow. I stuck with my initial approach.

###### Data layer
- For API, cache, and database, I ensured they were safe to be called from the main thread and ensured they moved to a
  background thread to perform their actions, to not block the main (or caller) thread.
- I made this layer act as the single source of truth for the application data. This also warranted moving user-selected
  filters to this layer.
- I also switched the existing use of `Dispatcher.Default` to `Dispatcher.IO` which is recommended for I/O operations.

###### Cache
- I employed the use of `ConcurrentMap` to ensure thread safety and prevent potential race conditions.

###### Database
- I used transactions to ensure both film and person entities were processed successfully before indicating a successful
  "mark as favorite" operation.
- I considered using `Mutex` to ensure thread safety, but it turned out to be redundant since the SQLite database used
  employs serialized threading mode.
- I leveraged Kotlin's type alias feature to rename the generated SDLDelight models into the naming I desired.

#### Future Improvements
- Make all use cases be provided by a single class. This will make constructors of view models that require several use
  cases not look verbose.
- Listen for changes to network availability to automatically load data if a user was previously offline. Having this will
  also prevent manually calling `getFilters` when updates are made to the favorite films in local storage.
- Managing dependency versioning from a single file using Gradle version catalog, to prevent having multiple dependency
  versions that can lead to conflicts.
- Using API fields and limits to retrieve only what is needed, hence increasing speed.

#### Other Out-of-Scope Improvements
- Sharing view models between Android and iOS: Maximizing code reuse and consistency across platforms.
- UI implementation: Adding more aesthetic designs and information that might be useful to users.
- Modularization: To enforce strict dependencies and prevent mistakenly breaking dependency structure. This also makes
  it easy to replace individual modules without affecting others, hence, this will make the code base more maintainable.
- Complete integration on iOS: After realizing it was not a requirement, I did not continue doing it due to time,
  but I was able to load the list of persons for the detail view.
