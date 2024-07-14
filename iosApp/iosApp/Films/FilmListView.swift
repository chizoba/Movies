//
//  FilmListView.swift
//  iosApp
//

import shared
import SwiftUI

struct FilmListView: View {
    let goToDetail: (String) -> Void

    @State private var viewModel = FilmListViewModel()

    var sortedFilms: [Film] {
        return viewModel.films.sorted { ($0.releaseDate) < ($1.releaseDate) }
    }
    
    var body: some View {
        ZStack {
            if viewModel.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
            } else if let errorMessage = viewModel.errorMessage {
                Text(errorMessage)
                    .padding(16)
            } else {
                ScrollView {
                    LazyVStack(spacing: 24) {
                        ForEach(sortedFilms) { film in
                            Button {
                                goToDetail(film.id)
                            } label: {
                                FilmListItem(
                                    title: film.title,
                                    desc: film.description_
                                )
                            }
                        }
                    }
                    .padding(16)
                }
            }
        }
        .transition(.opacity)
        .navigationTitle("Ghibli Films")
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Menu {
                    ForEach(viewModel.filters) { filter in
                        Button { Task { await viewModel.filter(filter) } } label: {
                            HStack {
                                Image(systemName: viewModel.selectedFilters.contains(filter) ? "checkmark.square" : "square")
                                Text(filter.name)
                            }
                        }
                    }
                } label: {
                    Image(systemName: "line.3.horizontal.decrease.circle.fill")
                }
            }
        }
        .refreshable { await viewModel.refresh() }
        .task { await viewModel.loadData() }
    }
}
