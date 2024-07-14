//
//  FilmListItem.swift
//  iosApp
//

import SwiftUI

struct FilmListItem: View {
    let title: String
    let desc: String
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(.title2)
                .padding(.bottom, 4)

            Text(desc)
                .font(.body)
                .foregroundColor(.black.opacity(0.75))
                .lineLimit(3)
                .multilineTextAlignment(.leading)
        }
        .padding(16)
        .clipShape(RoundedRectangle(cornerRadius: 12.0))
        .background(RoundedRectangle(cornerRadius: 12.0).fill(.white).shadow(radius: 4))
    }
}

#Preview {
    FilmListItem(
        title: "Castle in the Sky",
        desc: "The orphan Sheeta inherited a mysterious crystal that links her to the " +
            "mythical sky-kingdom of Laputa. With the help of resourceful Pazu and a " +
            "rollicking band of sky pirates, she makes her way to the ruins of the " +
            "once-great civilization. Sheeta and Pazu must outwit the evil Muska, who " +
            "plans to use Laputa's science to make himself ruler of the world."
    )
}
