package me.sdi.github.ribs.authorized.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.moshi.Moshi
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import me.sdi.github.R
import me.sdi.github.RxSchedulers
import me.sdi.github.network.UserInfoGateway
import me.sdi.github.ribs.TokenStorage
import me.sdi.github.ribs.authorized.UserStorage
import okhttp3.OkHttpClient
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.CLASS
import javax.inject.Qualifier
import javax.inject.Scope

/**
 * Builder for the {@link MainScope}.
 *
 * TODO describe this scope's responsibility as a whole.
 */
class MainBuilder(dependency: ParentComponent) :
    ViewBuilder<MainView, MainRouter, MainBuilder.ParentComponent>(dependency) {

    /**
     * Builds a new [MainRouter].
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new [MainRouter].
     */
    fun build(parentViewGroup: ViewGroup): MainRouter {
        val view = createView(parentViewGroup)
        val interactor = MainInteractor()
        val component = DaggerMainBuilder_Component.builder()
            .parentComponent(dependency)
            .view(view)
            .interactor(interactor)
            .build()
        return component.mainRouter()
    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): MainView? {
        return inflater.inflate(R.layout.main_view, parentViewGroup, false) as MainView
    }

    interface ParentComponent {
        fun okHttpClient(): OkHttpClient
        fun moshi(): Moshi
        fun tokenStorage(): TokenStorage
        fun userStorage(): UserStorage
        fun userInfoGateway(): UserInfoGateway
        fun rxSchedulers(): RxSchedulers
    }

    @dagger.Module
    abstract class Module {

        @MainScope
        @Binds
        internal abstract fun presenter(view: MainView): MainInteractor.MainPresenter

        @dagger.Module
        companion object {

            @MainScope
            @Provides
            @JvmStatic
            internal fun router(
                component: Component,
                view: MainView,
                interactor: MainInteractor
            ): MainRouter = MainRouter(view, interactor, component)
        }

        // TODO: Create provider methods for dependencies created by this Rib. These should be static.
    }

    @MainScope
    @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
    interface Component : InteractorBaseComponent<MainInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            fun interactor(interactor: MainInteractor): Builder

            @BindsInstance
            fun view(view: MainView): Builder

            fun parentComponent(component: ParentComponent): Builder
            fun build(): Component
        }
    }

    interface BuilderComponent {
        fun mainRouter(): MainRouter
    }

    @Scope
    @Retention(CLASS)
    internal annotation class MainScope

    @Qualifier
    @Retention(CLASS)
    internal annotation class MainInternal
}
