import shared
import SwiftUI

struct ContentView: View {
    @State private var showDetail: String?

    var body: some View {
        NavigationStack {
            FilmListView { showDetail = $0 }
                .navigationDestination(item: $showDetail) {
                    FilmDetailView(slug: $0)
                }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
