//
//  UseCases+Extensions.swift
//  iosApp
//

import shared

extension GetFilmDetailUseCase {
    func fetchFilm(_ slug: String) async throws -> FilmDetailUiModel {
        return try await withCheckedThrowingContinuation { continuation in
            invokeNative(slug: slug) { filmDetailUiModel in
                continuation.resume(returning: filmDetailUiModel)
            } onError: { error in
                continuation.resume(throwing: error.asError())
            }
        }
    }
}

