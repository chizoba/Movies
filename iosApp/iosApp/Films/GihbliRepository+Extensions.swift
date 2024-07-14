//
//  GihbliRepository+Extensions.swift
//  iosApp
//

import shared

extension GhibliRepository {
    func fetchFilms() async throws -> [Film] {
        try await withCheckedThrowingContinuation { continuation in
            getFilmsNative { films in
                continuation.resume(returning: films)
            } onError: { error in
                continuation.resume(throwing: error.asError())
            }
        }
    }

    func fetchSpecies() async throws -> [Specie] {
        try await withCheckedThrowingContinuation { continuation in
            getSpeciesNative { species in
                continuation.resume(returning: species)
            } onError: { error in
                continuation.resume(throwing: error.asError())
            }
        }
    }

    func filter(filmsWith species: [String]) async throws -> [Film] {
        try await withCheckedThrowingContinuation { continuation in
            filterFilmsNative(species: species) { species in
                continuation.resume(returning: species)
            } onError: { error in
                continuation.resume(throwing: error.asError())
            }
        }
    }
}
