//
//  FilmDetailView.swift
//  iosApp
//

import shared
import SwiftUI

struct FilmDetailView: View {
    let slug: String

    @State private var viewModel: FilmDetailViewModel = .init()

    var body: some View {
        ZStack {
            if viewModel.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                    .padding(.top, 24)
            } else if let errorMsg = viewModel.errorMessage {
                Text(errorMsg)
                    .padding(16)
            } else if let film = viewModel.film {
                List {
                    VStack(alignment: .leading, spacing: 16) {
                        DetailSection(title: "Title", desc: film.title)
                        DetailSection(title: "Description", desc: film.description_)
                        DetailSection(title: "Director", desc: film.director)
                        DetailSection(title: "Producer", desc: film.producer)
                        DetailSection(title: "Release Date", desc: film.releaseDate)
                        DetailSection(title: "Running Time", desc: film.runningTime)
                        DetailSection(title: "RT Score", desc: film.rtScore)
                        PeopleSection(title: "People", items: film.people)
                    }
                    .padding(16)
                    .listRowInsets(EdgeInsets())
                }
                .listStyle(PlainListStyle())
            }
        }
        .navigationTitle(viewModel.title)
        .task {
            await viewModel.loadData(slug: slug)
        }
    }
}

struct DetailSection: View {
    let title: String
    let desc: String
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Header(title: title)
            Text(desc)
        }
    }
}

struct PeopleSection: View {
    let title: String
    let items: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Header(title: title)
            ForEach(items, id: \.self) { item in
                Text(item)
                    .padding(.vertical, 4)
            }
        }
    }
}

struct Header: View {
    let title: String

    var body: some View {
        Text(title)
            .fontWeight(.semibold)
    }
}
