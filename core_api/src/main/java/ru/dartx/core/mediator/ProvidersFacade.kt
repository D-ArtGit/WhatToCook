package ru.dartx.core.mediator

import ru.dartx.core.database.DatabaseProvider
import ru.dartx.core.network.NetworkClientProvider

interface ProvidersFacade : AppProvider, DatabaseProvider, NetworkClientProvider