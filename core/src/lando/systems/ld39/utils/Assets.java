package lando.systems.ld39.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld39.utils.accessors.*;

/**
 * Created by Brian on 7/25/2017.
 */
public class Assets {

    public static AssetManager mgr;
    public static TweenManager tween;
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static GlyphLayout layout;
    public static BitmapFont font;
    public static ShaderProgram fontShader;

    public static Texture testTexture;
    public static Texture whitePixel;

    public static boolean initialized;

    public static void load() {
        initialized = false;

        final TextureLoader.TextureParameter linearParams = new TextureLoader.TextureParameter();
        linearParams.minFilter = Texture.TextureFilter.Linear;
        linearParams.magFilter = Texture.TextureFilter.Linear;

        final TextureLoader.TextureParameter nearestParams = new TextureLoader.TextureParameter();
        nearestParams.minFilter = Texture.TextureFilter.Nearest;
        nearestParams.magFilter = Texture.TextureFilter.Nearest;

        mgr = new AssetManager();
        mgr.load("images/badlogic.jpg", Texture.class, nearestParams);
        mgr.load("images/white-pixel.png", Texture.class, nearestParams);

        if (tween == null) {
            tween = new TweenManager();
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();
    }

    public static float update() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        testTexture = mgr.get("images/badlogic.jpg", Texture.class);
        whitePixel = mgr.get("images/white-pixel.png", Texture.class);

        final Texture distText = new Texture(Gdx.files.internal("fonts/ubuntu.png"), true);
        distText.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("fonts/ubuntu.fnt"), new TextureRegion(distText), false);
        font.getData().setScale(.3f);

        fontShader = loadShader("shaders/dist.vert", "shaders/dist.frag");

        return 1f;
    }

    public static void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        mgr.clear();
    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));
        ShaderProgram.pedantic = true;

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log:\n" + shaderProgram.getLog());
        }

        return shaderProgram;
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static void drawString(SpriteBatch batch, String text, float x, float y, Color c, float scale, BitmapFont font, float targetWidth, int halign){
        batch.setShader(fontShader);
        fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y, targetWidth, halign, true);
        font.getData().setScale(1f);
        fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

}
