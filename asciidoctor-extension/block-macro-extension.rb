Asciidoctor::Extensions.register do
  block_macro do
    use_dsl
    named :blokmacro

    process do |parent, target, attrs|

      create_image_block parent, attrs
    end
  end
end