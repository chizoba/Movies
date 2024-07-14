//
//  FilmListViewModel.swift
//  iosApp
//

import shared
import SwiftUI

extension Film: Identifiable {}

@Observable
class FilmListViewModel {
    @ObservationIgnored
    let repository: GhibliRepository = Inject.dependencies.repository

    var isLoading = true
    var errorMessage: String?
    var films: [Film] = []
    var filters: [Filter] = []
    var selectedFilters: [Filter] = []

    @MainActor
    func loadData() async {
        isLoading = true
        defer { isLoading = false }
        await refresh()
        filters = (try? await repository.fetchSpecies().map { Filter(id: $0.id, name: $0.name) }) ?? []
    }

    @MainActor
    func refresh() async {
        do {
            films = try await repository.fetchFilms()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    @MainActor
    func filter(_ filter: Filter) async {
        if selectedFilters.contains(filter) {
            selectedFilters = selectedFilters.filter { $0 != filter }
        } else {
            selectedFilters.append(filter)
        }
        if let filteredFilms = try? await repository.filter(filmsWith: selectedFilters.map { $0.id }) {
            films = filteredFilms
        }
    }
}

struct Filter: Identifiable, Equatable {
    let id: String
    let name: String
}
