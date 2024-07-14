//
//  FilmDetailViewModel.swift
//  iosApp
//

import shared
import SwiftUI

@Observable
class FilmDetailViewModel {
    @ObservationIgnored
    let repository: GhibliRepository = Inject.dependencies.repository
    @ObservationIgnored
    let coroutineContext: KotlinCoroutineContext = Inject.dependencies.backgroundContext

    @ObservationIgnored
    var getPeopleUseCase: GetPeopleUseCase
    @ObservationIgnored
    var getFilmDetailUseCase: GetFilmDetailUseCase
    @ObservationIgnored
    var toggleFavoriteUseCase: ToggleFavoriteUseCase
    @ObservationIgnored
    var observeFavoriteFilmsUseCase: ObserveFavoriteFilmsUseCase
    
    var isLoading = true
    var errorMessage: String?
    var title: String = "-"
    var film: FilmDetailUiModel?

    init() {
        self.getPeopleUseCase = GetPeopleUseCase(
            repository: repository,
            context: coroutineContext
        )
        self.getFilmDetailUseCase = GetFilmDetailUseCase(
            repository: repository,
            context: coroutineContext,
            getPeopleUseCase: getPeopleUseCase
        )
        self.toggleFavoriteUseCase = ToggleFavoriteUseCase(
            repository: repository,
            context: coroutineContext,
            getPeopleUseCase: getPeopleUseCase
        )
        self.observeFavoriteFilmsUseCase = ObserveFavoriteFilmsUseCase(
            repository: repository,
            context: coroutineContext
        )
        title = title
    }

    @MainActor
    func loadData(slug: String) async {
        isLoading = true
        defer { isLoading = false }
        do {
            let film = try await getFilmDetailUseCase.fetchFilm(slug)
            self.film = film
            title = film.title
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
